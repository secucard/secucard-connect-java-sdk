package com.secucard.connect.channel.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.secucard.connect.Callback;
import com.secucard.connect.ServerErrorException;
import com.secucard.connect.channel.JsonMappingException;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.QueryParams;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.transport.Status;
import com.secucard.connect.util.jackson.DynamicTypeReference;
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

public class RestChannel extends RestChannelBase {
  protected javax.ws.rs.client.Client restClient;

  public RestChannel(String id, Configuration cfg) {
    super(cfg, id);
  }

  @Override
  public synchronized void open() {
    // rest client should be initialized just one time, client is expensive
    if (restClient == null) {
      LOG.debug("REST channel initialized.");
      initClient();
    }
  }

  private void initClient() {
    restClient = ClientBuilder.newClient();
//    restClient.register(new LoggingFilter(LOG, false));

    // only used to auto map request entities to json,
    // response mapping back is done 'manually' because this provider lacks flexibility
    restClient.register(JacksonJsonProvider.class);
    restClient.property(ClientProperties.CONNECT_TIMEOUT, 30 * 1000);
    restClient.property(ClientProperties.READ_TIMEOUT, 3 * 60 * 1000);
  }

  @Override
  public <T> T post(String url, Map<String, Object> parameters, Map<String, String> headers, Class<T> responseType) {
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
    try {
      Future<Response> future = invocation.submit();
      Response response = future.get(configuration.responseTimeoutSec, TimeUnit.SECONDS);
      if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
        throw new WebApplicationException(response);
      }
      return mapEntity(response, new DynamicTypeReference<T>(responseType));
    } catch (Throwable e) {
      if (e instanceof WebApplicationException) {
        Response response = ((WebApplicationException) e).getResponse();
        Map entity = response.readEntity(Map.class);
        throw new HttpErrorException("Request failed.", e, response.getStatus(), entity);
      }

      throw new RuntimeException("Error executing request", e);
    }
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

  @Override
  public <T> T get(Class<T> type, String objectId, Callback<T> callback) {
    Invocation invocation = builder(type, null, objectId).buildGet();
    return getResponse(invocation, new DynamicTypeReference(type), callback);
  }

  @Override
  public <T> ObjectList<T> getList(Class<T> type, QueryParams queryParams, Callback<ObjectList<T>> callback) {
    Invocation invocation = builder(type, queryParamsToMap(queryParams)).buildGet();
    return getResponse(invocation, new DynamicTypeReference(ObjectList.class, type), callback,
        Response.Status.NOT_FOUND.getStatusCode());
  }

  @Override
  public <T> T create(T object, Callback<T> callback) {
    Entity<T> entity = Entity.json(object);
    Invocation invocation = builder(object.getClass(), null).buildPost(entity);
    return getResponse(invocation, new DynamicTypeReference(object.getClass()), callback);
  }

  @Override
  public <T extends SecuObject> T update(T object, Callback<T> callback) {
    Entity<T> entity = Entity.json(object);
    Invocation invocation = builder(object.getClass(), null, object.getId()).buildPut(entity);
    return getResponse(invocation, new DynamicTypeReference(object.getClass()), callback);
  }

  @Override
  public <T> T update(Class product, String objectId, String action, String actionArg, Object arg,
                      Class<T> returnType, Callback<T> callback) {
    Entity entity = Entity.json(arg);
    Invocation invocation = builder(product, null, objectId, action, actionArg).buildPut(entity);
    return getResponse(invocation, new DynamicTypeReference(returnType), callback);
  }

  @Override
  public void delete(Class type, String objectId, Callback<Void> callback) {
    Invocation invocation = builder(type, null, objectId).buildDelete();
    getResponse(invocation, null, callback);
  }

  @Override
  public void delete(Class product, String objectId, String action, String actionArg, Callback<Void> callback) {
    Invocation invocation = builder(product, null, objectId, action, actionArg).buildDelete();
    getResponse(invocation, null, callback);
  }

  @Override
  public <T> T execute(Class product, String objectId, String action, String actionArg, Object arg, Class<T> returnType,
                       Callback<T> callback) {
    Entity entity = Entity.json(arg);
    Invocation invocation = builder(product, null, objectId, action, actionArg).buildPost(entity);
    return getResponse(invocation, new DynamicTypeReference(returnType), callback);
  }

  public <T> T execute(String appId, String command, Object arg, Class<T> returnType, Callback<T> callback) {
    Entity entity = Entity.json(arg);

    open();

    // todo: Cache targets?
    WebTarget target = restClient.target(configuration.baseUrl);

    if (appId != null) {
      target = target.path(pathResolver.resolveAppId(appId, '/'));
    }

    target = target.path(command);

    Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON);

    setupAuth(builder);

    return getResponse(builder.buildPost(entity), new DynamicTypeReference(returnType), callback);
  }


  @Override
  public synchronized void close() {
    if (restClient != null) {
      restClient.close();
      restClient = null;
      LOG.debug("REST channel closed.");
    }
  }

  // private -------------------------------------------------------------------------------------------------------------------

  private <T> Invocation.Builder builder(Class<T> type, Map<String, Object> queryParams, String... pathArgs) {
    open();

    // todo: Cache targets?
    WebTarget target = restClient.target(configuration.baseUrl);

    if (type != null) {
      target = target.path(pathResolver.resolveType(type, '/'));
    }

    for (String path : pathArgs) {
      if (path != null) {
        target = target.path(path);
      }
    }

    if (queryParams != null) {
      for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
        target = target.queryParam(entry.getKey(), entry.getValue());
      }
    }

    Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON);

    setupAuth(builder);

    return builder;
  }

  private void setupAuth(Invocation.Builder builder) {
    MultivaluedHashMap<String, Object> headers = new MultivaluedHashMap<>();
    setAuthorizationHeader(headers);
    if (headers.size() > 0) {
      builder.headers(headers);
    }
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
    for (Integer st : ignoredStatus) {
      if (st != null && response.getStatus() == st) {
        // ignore exception and return null
        LOG.debug("Ignored error response: ", response.getStatus(), ", ", response.readEntity(String.class));
        return null;
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
    return jsonMapper.map(json, entityType);
  }

  /**
   * Unwrapping and translating throwable.
   */
  private <T> RuntimeException translate(Throwable throwable) {
    // check if this is a regular server error and read error status
    if (throwable instanceof WebApplicationException) {
      Response response = ((WebApplicationException) throwable).getResponse();

      Status status = null;
      try {
        status = mapEntity(response, new TypeReference<Status>() {
        });
      } catch (Exception e) {
        // ignore
      }

      if (status != null) {
        return new ServerErrorException(status.getCode(), status.getErrorDetails(), status.getErrorUser(),
            status.getError(), status.getSupportId(), throwable);
      } else {
        return new HttpErrorException("Request failed.", throwable, response.getStatus());
      }
    }

    if (throwable instanceof JsonMappingException) {
      return new ServerErrorException("Unexpected secucard server response: " + ((JsonMappingException) throwable).getJson());
    }

    if (throwable instanceof ExecutionException) {
      throwable = throwable.getCause();
    }

    if (throwable instanceof RuntimeException) {
      return (RuntimeException) throwable;
    }

    // just wrap in any runtime ex
    return new RuntimeException("Error executing request.", throwable);
  }
}
