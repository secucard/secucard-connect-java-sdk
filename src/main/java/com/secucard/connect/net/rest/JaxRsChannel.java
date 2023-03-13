/*
 * Copyright (c) 2015. hp.weber GmbH & Co secucard KG (www.secucard.com)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.secucard.connect.net.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.secucard.connect.client.Callback;
import com.secucard.connect.client.ClientContext;
import com.secucard.connect.client.ClientError;
import com.secucard.connect.client.NetworkError;
import com.secucard.connect.net.Options;
import com.secucard.connect.net.ServerErrorException;
import com.secucard.connect.net.util.jackson.DynamicTypeReference;
import com.secucard.connect.product.common.model.ObjectList;
import com.secucard.connect.product.common.model.Status;
import com.secucard.connect.util.ExceptionMapper;
import com.secucard.connect.util.Log;
import org.glassfish.jersey.client.ClientProperties;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class JaxRsChannel extends RestChannel {
  protected final static Log LOG = new Log(JaxRsChannel.class);

  protected Client restClient;

  public JaxRsChannel(Configuration configuration, ClientContext context) {
    super(configuration, context);
  }


  public <T> T request(Method method, Params params, Callback<T> callback) {
    return getResponse(false, method, params, callback);
  }

  public <T> ObjectList<T> requestList(Method method, Params params, Callback<ObjectList<T>> callback) {
    return getResponse(true, method, params, callback);
  }

  private <T> T getResponse(boolean returnList, Method method, Params params, final Callback<T> callback) {
    if (params.options == null) {
      params.options = Options.getDefault();
    }

    open();

    Invocation.Builder builder = builder(params);

    Entity<Object> entity = null;
    if (params.data != null) {
      entity = Entity.json(params.data);
    }

    Invocation invocation;
    if (method == Method.GET) {
      invocation = builder.buildGet();
    } else if (method == Method.CREATE || method == Method.EXECUTE) {
      invocation = builder.buildPost(entity);
    } else if (method == Method.UPDATE) {
      invocation = builder.buildPut(entity);
    } else if (method == Method.DELETE) {
      invocation = builder.buildDelete();
    } else {
      throw new IllegalArgumentException("Invalid method arg");
    }

    DynamicTypeReference ref = null;
    if (params.returnType != null) {
      if (returnList) {
        ref = new DynamicTypeReference(ObjectList.class, params.returnType);
      } else {
        ref = new DynamicTypeReference(params.returnType);
      }
    }


    /* not necessary anymore, not sure...
    Integer[] ignoredStatus = null;

    if (params.queryParams != null) {
      // let 404 be valid for object queries
      ignoredStatus = new Integer[]{Response.Status.NOT_FOUND.getStatusCode()};
    }*/

    return getResponse(invocation, ref, callback);
  }

  @Override
  public <T, E> T post(String url, Map<String, Object> parameters, Map<String, String> headers, Class<T> responseType,
                       Class<E> errorResponseType) throws HttpErrorException {
    LOG.debug("Executing post request to: ", url, "; params: ", parameters, "; header: ", headers);
    open();

    Invocation.Builder builder = restClient.target(url).request(MediaType.APPLICATION_FORM_URLENCODED);
    if (headers != null) {
      builder.headers(new MultivaluedHashMap<String, Object>(headers));
    }
    MultivaluedHashMap<String, String> map = new MultivaluedHashMap<>();
    for (Map.Entry<String, Object> entry : parameters.entrySet()) {
      Object value = entry.getValue();
      String key = entry.getKey();
      if (value instanceof List) {
        map.put(key, (List) value);
      } else if (value instanceof String[]) {
        map.put(key, Arrays.asList((String[]) value));
      } else {
        map.putSingle(key, (String) value);
      }
    }
    Invocation invocation = builder.buildPost(Entity.form(map));


    Response response;
    try {
      response = invocation.submit().get(configuration.responseTimeoutSec, TimeUnit.SECONDS);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      throw translate(e);
    }

    if (!response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
      Object entity = null;
      if (errorResponseType != null) {
        try {
          entity = response.readEntity(errorResponseType);
        } catch (Exception e) {
          LOG.error("Error mapping response to type ", errorResponseType);
        }
      }
      throw new HttpErrorException(response.getStatus(), entity);
    }

    if (responseType != null) {
      return response.readEntity(responseType);
    }

    return null;
  }

  @Override
  public InputStream getStream(String url, Map<String, Object> parameters, Map<String, String> headers,
                               Callback<InputStream> callback) {
    open();
    WebTarget target = restClient.target(url);
    if (parameters != null) {
      for (Map.Entry<String, Object> entry : parameters.entrySet()) {
        target.queryParam(entry.getKey(), entry.getValue());
      }
    }
    Invocation.Builder builder = target.request();
    if (headers != null) {
      builder.headers(new MultivaluedHashMap<String, Object>(headers));
    }

    Response response = builder.get();

    if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
      // no secucard api specific error expected here, just basic http error
      throw new WebApplicationException(response.getStatus());
    }

    Object entity = response.getEntity();
    if (entity instanceof InputStream) {
      return (InputStream) entity;
    }
    return null;
  }

  public synchronized void open() {
    // rest client should be initialized just one time, client is expensive
    if (restClient == null) {
      LOG.debug("REST channel not initialized.");
      initClient();
    }
  }

  @Override
  public synchronized void close() {
    if (restClient != null) {
      restClient.close();
      restClient = null;
      LOG.debug("REST channel closed.");
    }
  }

  private void initClient() {
    restClient = ClientBuilder.newClient();
//    restClient.register(new LoggingFilter(LOG, false));

    // only used to auto map request entities to json,
    // response mapping back is done 'manually' because this provider lacks flexibility
    restClient.register(JacksonJsonProvider.class);

    restClient.property(ClientProperties.CONNECT_TIMEOUT, configuration.connectTimeoutSec * 1000);
    restClient.property(ClientProperties.READ_TIMEOUT, configuration.responseTimeoutSec * 1000);
  }

  private <T> Invocation.Builder builder(Params params) {
    WebTarget target = restClient.target(configuration.baseUrl);

    if (params.appId != null) {
      target = target.path("General").path("Apps").path(params.appId).path("callBackend");
    } else if (params.object != null) {
      target = target.path(buildTarget(params.object, '/'));
    } else {
      throw new IllegalArgumentException("Missing object spec or app id.");
    }

    if (params.objectId != null) {
      target = target.path(params.objectId);
    }

    if (params.action != null) {
      target = target.path(params.action);
    }

    if (params.actionArg != null) {
      target = target.path(params.actionArg);
    }

    if (params.queryParams != null) {
      for (Map.Entry<String, Object> entry : queryParamsToMap(params.queryParams).entrySet()) {
        target = target.queryParam(entry.getKey(), entry.getValue());
      }
    }

    Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON);

    MultivaluedHashMap<String, Object> headers = new MultivaluedHashMap<>();
    if (!params.options.anonymous) {
      setAuthorizationHeader(headers);
    }

    setIdempotencyIdHeader(params, headers);

    if (headers.size() > 0) {
      builder.headers(headers);
    }

    return builder;

  }

  private <T> T getResponse(Invocation invocation, final TypeReference entityType, final Callback<T> callback,
                            final Integer... ignoredStatus) {

    LOG.debug("Executing request for: ", entityType.getType().toString());
    T result = null;
    if (callback == null) {
      Future<Response> future = invocation.submit();
      try {
        Response response = future.get(configuration.responseTimeoutSec, TimeUnit.SECONDS);
        result = readEntity(response, entityType, ignoredStatus);
      } catch (Throwable e) {
        LOG.info(e.toString());
        throw translate(e);
      }
    } else {
      invocation.submit(
          new InvocationCallback<Response>() {
            @Override
            public void completed(Response response) {
              T result;
              try {
                result = readEntity(response, entityType, ignoredStatus);
              } catch (Throwable e) {
                LOG.info(e.toString());
                callback.failed(translate(e));
                return;
              }
              callback.completed(result);
            }

            @Override
            public void failed(Throwable throwable) {
              callback.failed(translate(throwable));
            }
          });
    }
    return result;
  }

  private <T> T readEntity(Response response, TypeReference entityType, Integer... ignoredStatus)
      throws WebApplicationException, IOException {
    if (ignoredStatus != null) {
      for (Integer st : ignoredStatus) {
        if (st != null && response.getStatus() == st) {
          // ignore exception and return null
          return null;
        }
      }
    }

    if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
      throw new WebApplicationException(response);
    }

    T result = null;
    if (entityType != null) {
      result = mapEntity(response, entityType);
    }
    return result;
  }

  private <T> T mapEntity(Response response, TypeReference entityType) throws IOException {
    String json = response.readEntity(String.class);
    return context.jsonMapper.map(json, entityType);
  }

  /**
   * Unwrapping and translating throwable.
   */
  protected RuntimeException translate(Throwable throwable) {
    RuntimeException exception = super.translate(throwable);
    if (exception != null) {
      return exception;
    }

    IllegalStateException ie = ExceptionMapper.unwrap(throwable, IllegalStateException.class);
    if (ie != null && ie.getMessage().contains("Already connected")) {
      // weird error thrown when trying to send request without connection
      // https://java.net/jira/browse/JERSEY-2728
      return new NetworkError(throwable);
    }

    WebApplicationException we = ExceptionMapper.unwrap(throwable, WebApplicationException.class);
    if (we != null) {
      //this is a HTTP error, try to get status payload
      Response response = we.getResponse();

      Status status = null;
      try {
        status = mapEntity(response, new TypeReference<Status>() {
        });
      } catch (Exception e) {
        // ignore
      }

      if (status == null) {
        return new ClientError("Request failed with HTTP error " + response.getStatus(), throwable);
      }

      return new ServerErrorException(status, throwable);
    }


    // just wrap in any runtime ex
    return new ClientError("Error executing request.", throwable);
  }

}
