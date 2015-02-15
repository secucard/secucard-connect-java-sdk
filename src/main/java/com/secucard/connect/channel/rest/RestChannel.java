package com.secucard.connect.channel.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.secucard.connect.Callback;
import com.secucard.connect.SecuException;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.QueryParams;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.transport.Status;
import com.secucard.connect.util.jackson.DynamicTypeReference;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public class RestChannel extends RestChannelBase {
  protected javax.ws.rs.client.Client restClient;
  protected AuthFilter authFilter = new AuthFilter();
  private final boolean secure = true;

  public RestChannel(String id, Configuration cfg) {
    super(cfg, id);
  }

  @Override
  public void open(Callback callback) {
    try {
      // rest client should be initialized just one time, client is expensive
      initClient();
      onCompleted(callback, null);
    } catch (Throwable e) {
      if (callback == null) {
        throw e;
      }
      onFailed(callback, e);
    }
  }

  private void initClient() {
    restClient = ClientBuilder.newClient();
//    restClient.register(new LoggingFilter(LOG, false));

    // only used to auto map request entities to json,
    // response mapping back is done 'manually' because this provider lacks flexibility
    restClient.register(JacksonJsonProvider.class);
  }

  @Override
  public <T> T post(String url, Map<String, Object> parameters, Map<String, String> headers, Class<T> responseType,
                    Integer... ignoredState) {
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
    return getResponse(invocation, new DynamicTypeReference(responseType), null, ignoredState);
  }


  @Override
  public InputStream getStream(String url, Map<String, Object> parameters, Map<String, String> headers) {
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

    Object entity = builder.get().getEntity();
    if (entity instanceof InputStream) {
      return (InputStream) entity;
    }
    return null;
  }

  @Override
  public <T> T getObject(Class<T> type, String objectId, Callback<T> callback) {
    Invocation invocation = builder(type, null, secure, objectId).buildGet();
    return getResponse(invocation, new DynamicTypeReference(type), callback);
  }

  @Override
  public <T> ObjectList<T> findObjects(Class<T> type, QueryParams queryParams, Callback<ObjectList<T>> callback) {
    Invocation invocation = builder(type, queryParamsToMap(queryParams), secure).buildGet();
    return getResponse(invocation, new DynamicTypeReference(ObjectList.class, type), callback,
        Response.Status.NOT_FOUND.getStatusCode());
  }

  @Override
  public <T> T createObject(T object, Callback<T> callback) {
    Entity<T> entity = Entity.json(object);
    Invocation invocation = builder(object.getClass(), null, secure).buildPost(entity);
    return getResponse(invocation, new DynamicTypeReference(object.getClass()), callback);
  }

  @Override
  public <T extends SecuObject> T updateObject(T object, Callback<T> callback) {
    Entity<T> entity = Entity.json(object);
    Invocation invocation = builder(object.getClass(), null, secure, object.getId()).buildPut(entity);
    return getResponse(invocation, new DynamicTypeReference(object.getClass()), callback);
  }

  @Override
  public <T> T updateObject(Class product, String objectId, String action, String actionArg, Object arg,
                            Class<T> returnType, Callback<T> callback) {
    Entity entity = Entity.json(arg);
    Invocation invocation = builder(product, null, secure, objectId, action, actionArg).buildPut(entity);
    return getResponse(invocation, new DynamicTypeReference(returnType), callback);
  }

  @Override
  public void deleteObject(Class type, String objectId, Callback<?> callback) {
    Invocation invocation = builder(type, null, secure, objectId).buildDelete();
    getResponse(invocation, null, callback);
  }

  @Override
  public void deleteObject(Class product, String objectId, String action, String actionArg, Callback<?> callback) {
    Invocation invocation = builder(product, null, secure, objectId, action, actionArg).buildDelete();
    getResponse(invocation, null, callback);
  }

  @Override
  public <T> T execute(Class product, String objectId, String action, String actionArg, Object arg, Class<T> returnType, Callback<T> callback) {
    Entity entity = Entity.json(arg);
    Invocation invocation = builder(product, null, secure, objectId, action, actionArg).buildPost(entity);
    return getResponse(invocation, new DynamicTypeReference(returnType), callback);
  }

  public <T> T execute(String appId, String command, Object arg, Class<T> returnType, Callback<T> callback) {
    Entity entity = Entity.json(arg);

    // todo: Cache targets?
    WebTarget target = restClient.target(configuration.getBaseUrl());

    if (appId != null) {
      target = target.path(pathResolver.resolveAppId(appId, '/'));
    }

    target = target.path(command);

    if (secure) {
      target.register(authFilter);
    }

    Invocation invocation = target.request(MediaType.APPLICATION_JSON).buildPost(entity);
    return getResponse(invocation, new DynamicTypeReference(returnType), callback);
  }


  @Override
  public void close(Callback callback) {
    try {
      restClient.close();
      onCompleted(callback, null);
    } catch (Throwable e) {
      if (callback == null) {
        throw e;
      }
      onFailed(callback, e);
    }
  }

  // private -------------------------------------------------------------------------------------------------------------------

  private <T> Invocation.Builder builder(Class<T> type, Map<String, Object> queryParams, boolean secure,
                                         String... pathArgs) {
    if (restClient == null) {
      throw new IllegalStateException("REST client not initialized.");
    }

    // todo: Cache targets?
    WebTarget target = restClient.target(configuration.getBaseUrl());

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

    if (secure) {
      target.register(authFilter);
    }

    return target.request(MediaType.APPLICATION_JSON);
  }

  private <T> T getResponse(Invocation invocation, final TypeReference entityType, final Callback<T> callback,
                            final Integer... ignoredStatus) {
    T result = null;
    if (callback == null) {
      Future<Response> future = invocation.submit();
      try {
        Response response = future.get();
        result = readEntity(response, entityType, ignoredStatus);
      } catch (Throwable e) {
        throw translate(e);
      }
    } else {
      invocation.submit(
          new InvocationCallback<Response>() {
            @Override
            public void completed(Response response) {
              T result = null;
              try {
                result = readEntity(response, entityType, ignoredStatus);
              } catch (Exception e) {
                failed(e);
                return;
              }
              onCompleted(callback, result);
            }

            @Override
            public void failed(Throwable throwable) {
              onFailed(callback, throwable);
            }
          });
    }
    return result;
  }

  private <T> T readEntity(Response response, TypeReference entityType, Integer... ignoredStatus) throws IOException {
    for (Integer st : ignoredStatus) {
      if (st != null && response.getStatus() == st) {
        // ignore exception and return null
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
    return jsonMapper.map(response.readEntity(String.class), entityType);
  }

  private RuntimeException translate(Throwable throwable) {
    Status status = null;
    if (throwable instanceof WebApplicationException) {
      Response response = ((WebApplicationException) throwable).getResponse();
      try {
        status = mapEntity(response, new TypeReference<Status>() {
        });
      } catch (Exception e) {
        // treat as no response
        LOG.severe(e.getMessage());
      }
    }
    if (status != null) {
      return new SecuException(status, throwable);
    } else {
      return new SecuException(throwable);
    }
  }

  /**
   * Filter for supplying the access token to the end point
   */
  @Provider
  private class AuthFilter implements ClientRequestFilter {

    @Override
    public void filter(ClientRequestContext context) throws IOException {
      setAuthorizationHeader(context.getHeaders());
    }
  }
}
