package com.secucard.connect.channel.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.secucard.connect.Callback;
import com.secucard.connect.SecuException;
import com.secucard.connect.auth.AuthProvider;
import com.secucard.connect.auth.OAuthClientCredentials;
import com.secucard.connect.auth.OAuthUserCredentials;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.auth.Token;
import com.secucard.connect.model.transport.QueryParams;
import com.secucard.connect.model.transport.Status;
import com.secucard.connect.util.jackson.DynamicTypeReference;
import org.glassfish.jersey.filter.LoggingFilter;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Future;

public class RestChannel extends RestChannelBase implements AuthProvider {
  protected javax.ws.rs.client.Client restClient;
  protected LoginFilter loginFilter;
  private final boolean secure = true;

  public RestChannel(String id, Configuration cfg) {
    super(cfg, id);
    loginFilter = new LoginFilter(this);
  }

  @Override
  public void open(Callback callback) {
    try {
      // rest client should be initialized just on time, client is expensive
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
  public synchronized Token getToken() {
    Token token = (Token) storage.get("token" + id);
    Long expireTime = (Long) storage.get("expireTime" + id);
    if (token == null) {
      token = createToken(configuration.getClientCredentials(), configuration.getUserCredentials(), null);
    } else if (expireTime != null && expireTime < System.currentTimeMillis() - 30 * 1000) {
      token = createToken(configuration.getClientCredentials(), null, token.getRefreshToken());
    }
    expireTime = System.currentTimeMillis() + token.getExpiresIn() * 1000;
    storage.save("token" + id, token);
    storage.save("expireTime" + id, expireTime);
    return token;
  }

  private Token createToken(OAuthClientCredentials clientCredentials, OAuthUserCredentials userCredentials,
                            String refreshToken) {
    Map<String, String> parameters = createAuthParams(clientCredentials, userCredentials, refreshToken, null);

    Invocation.Builder builder = restClient.target(configuration.getOauthUrl()).request(MediaType.APPLICATION_FORM_URLENCODED);
    builder.header(HttpHeaders.USER_AGENT, userAgentProvider.getValue());
    Invocation invocation = builder.buildPost(Entity.form(new MultivaluedHashMap<>(parameters)));

    return getResponse(invocation, new DynamicTypeReference(Token.class), null);
  }

  @Override
  public String invoke(String command, Callback<String> callback) {
    Invocation invocation = builder(null, null, secure, command).buildGet();
    return getResponse(invocation, new DynamicTypeReference(String.class), callback);
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
  public <T> T updateObject(Class type, String objectId, String action, String actionArg, Object arg,
                            Class<T> returnType, Callback<T> callback) {
    Entity entity = Entity.json(arg);
    Invocation invocation = builder(type, null, secure, objectId, action, actionArg).buildPut(entity);
    return getResponse(invocation, new DynamicTypeReference(returnType), callback);
  }

  @Override
  public void deleteObject(Class type, String objectId, Callback<?> callback) {
    Invocation invocation = builder(type, null, secure, objectId).buildDelete();
    getResponse(invocation, null, callback);
  }

  @Override
  public void deleteObject(Class type, String objectId, String action, String actionArg, Callback<?> callback) {
    Invocation invocation = builder(type, null, secure, objectId, action, actionArg).buildDelete();
    getResponse(invocation, null, callback);
  }

  @Override
  public <T> T execute(Class type, String objectId, String action, String actionArg, Object arg, Class<T> returnType, Callback<T> callback) {
    Entity entity = Entity.json(arg);
    Invocation invocation = builder(type, null, secure, objectId, action, actionArg).buildPut(entity);
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
      target.register(loginFilter);
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

  private <T> Invocation.Builder builder(Class<T> type, Map<String, String> queryParams, boolean secure,
                                         String... pathArgs) {
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
      for (Map.Entry<String, String> entry : queryParams.entrySet()) {
        target = target.queryParam(entry.getKey(), entry.getValue());
      }
    }

    if (secure) {
      target.register(loginFilter);
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
      } catch (Exception e) {
        throw translate(e);
      }
    } else {
      invocation.submit(
          new InvocationCallback<Response>() {
            @Override
            public void completed(Response response) {
              T result = null;
              try {
                result = readEntity(response, entityType);
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
      if (response.getStatus() == st) {
        // ignore exception and return null
        return null;
      }
    }

    if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
      throw new WebApplicationException(response);
    }

    T result = null;
    if (entityType != null) {
      result = readEntity(response, entityType);
    }
    return result;
  }

  private <T> T readEntity(Response response, TypeReference entityType) throws IOException {
    return jsonMapper.map(response.readEntity(String.class), entityType);
  }

  private SecuException translate(Throwable throwable) {
    Status status = null;
    if (throwable instanceof WebApplicationException) {
      Response response = ((WebApplicationException) throwable).getResponse();
      try {
        status = readEntity(response, new TypeReference<Status>() {
        });
      } catch (Exception e) {
        // treat as no response
      }
    }
    if (status != null) {
      return new SecuException(status.getErrorDetails(), throwable);
    } else {
      return new SecuException(throwable);
    }
  }
}
