package com.secucard.connect.channel.rest;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.secucard.connect.SecuException;
import com.secucard.connect.auth.AuthProvider;
import com.secucard.connect.auth.OAuthClientCredentials;
import com.secucard.connect.auth.OAuthUserCredentials;
import com.secucard.connect.channel.AbstractChannel;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.auth.Token;
import com.secucard.connect.model.transport.QueryParams;
import com.secucard.connect.model.transport.Status;
import com.secucard.connect.storage.DataStorage;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.*;

public class RestChannel extends AbstractChannel implements AuthProvider {
  private javax.ws.rs.client.Client restClient;
  private GenericTypeResolver typeResolver;
  private LoginFilter loginFilter;
  private final Configuration cfg;
  private DataStorage storage;
  private String id;
  private UserAgentProvider userAgentProvider;

  public RestChannel(String id, Configuration cfg) {
    this.cfg = cfg;
    this.restClient = ClientBuilder.newClient();
    restClient.register(JacksonJsonProvider.class);
    loginFilter = new LoginFilter(this);
    this.id = id;
  }

  public void setStorage(DataStorage storage) {
    this.storage = storage;
  }

  public void setTypeResolver(GenericTypeResolver typeResolver) {
    this.typeResolver = typeResolver;
  }

  public void setUserAgentProvider(UserAgentProvider userAgentProvider) {
    this.userAgentProvider = userAgentProvider;
  }

  @Override
  public void open() {
    // nothing to do
  }

  @Override
  public synchronized Token getToken() {
    Token token = storage.get("token" + id);
    Long expireTime = storage.get("expireTime" + id);
    if (token == null) {
      token = createToken(cfg.getClientCredentials(), null, null);
    } else if (expireTime != null && expireTime < System.currentTimeMillis() - 30 * 1000) {
      token = createToken(cfg.getClientCredentials(), null, token.getRefreshToken());
    }
    expireTime = System.currentTimeMillis() + token.getExpiresIn() * 1000;
    storage.save("token" + id, token);
    storage.save("expireTime" + id, expireTime);
    return token;
  }

  private Token createToken(OAuthClientCredentials clientCredentials, OAuthUserCredentials userCredentials,
                            String refreshToken) {
    MultivaluedMap<String, String> parameters = new MultivaluedHashMap<>();
    parameters.add("client_id", clientCredentials.getClientId());
    parameters.add("client_secret", clientCredentials.getClientSecret());
    if (refreshToken != null) {
      parameters.add("grant_type", "refresh_token");
      parameters.add("refresh_token", refreshToken);
    } else if (userCredentials != null) {
      parameters.add("grant_type", "appuser");
      parameters.add("username", userCredentials.getUsername());
      parameters.add("password", userCredentials.getPassword());
    } else {
      parameters.add("grant_type", "client_credentials");
    }
    WebTarget target = restClient.target(cfg.getOauthUrl());
    Invocation.Builder request = target.request(MediaType.APPLICATION_FORM_URLENCODED);
    request.header(HttpHeaders.USER_AGENT, userAgentProvider.getValue());
    Response response = request.post(Entity.form(parameters));
    response.bufferEntity();
    if (response.getStatus() != Response.Status.OK.getStatusCode()) {
      String entity = response.readEntity(String.class);
      throw new SecuException("Error authenticating client: " +  entity);
    }
    try {
      return response.readEntity(Token.class);
    } catch (Exception e) {
      throw new SecuException("Error reading authentication data.", e);
    }
  }

  @Override
  public <T extends SecuObject> ObjectList<T> findObjects(Class<T> type, QueryParams q) {
    WebTarget target = getTarget(type, null, null, true);
    Response response = executeRequest(target, "GET", null);

    handleResponseNot(response, Response.Status.NOT_FOUND, Response.Status.OK);
    if (Response.Status.NOT_FOUND.getStatusCode() == response.getStatus()) {
      return null;
    }

    return readObjects(response, typeResolver.getGenericType(type));
  }

  @Override
  public <T extends SecuObject> T getObject(Class<T> type, String objectId) {
    WebTarget target = getTarget(type, objectId, null, true);
    Response response = executeRequest(target, "GET", null);

    handleResponseNot(response, Response.Status.NOT_FOUND, Response.Status.OK);
    if (Response.Status.NOT_FOUND.getStatusCode() == response.getStatus()) {
      return null;
    }

    return readObject(response, new GenericType<T>(type));
  }

  @Override
  public <T extends SecuObject> T saveObject(T object) {
    WebTarget target = getTarget(object.getClass(), object.getId(), null, true);
    Response response = executeRequest(target, object.getId() == null ? "POST" : "PUT", object);
    handleResponseNot(response, Response.Status.OK);
    return readObject(response, new GenericType<T>(object.getClass()));
  }

  @Override
  public <T extends SecuObject> boolean deleteObject(Class<T> type, String objectId) {
    WebTarget target = getTarget(type, objectId, null, true);
    Response response = executeRequest(target, "DELETE", null);
    handleResponseNot(response, Response.Status.NOT_FOUND, Response.Status.OK);
    return Response.Status.NOT_FOUND.getStatusCode() == response.getStatus();
  }

  @Override
  public void setEventListener(EventListener listener) {
    throw new UnsupportedOperationException();
  }

  @Override
  public <A, R> R execute(String action, String[] ids, A arg, Class<R> returnType) {
    WebTarget target = getTarget(arg.getClass(), ids[0], action, true);
    Response response = executeRequest(target, "POST", null);

    handleResponseNot(response, Response.Status.NOT_FOUND, Response.Status.OK);
    if (Response.Status.NOT_FOUND.getStatusCode() == response.getStatus()) {
      return null;
    }

    return readObject(response, new GenericType<R>(returnType));
  }

  @Override
  public void invoke(String command) {
    WebTarget target = restClient.target(cfg.getBaseUrl()).path(command);
    target.request().get();
  }

  @Override
  public void close() {
    restClient.close();
  }

  // private -------------------------------------------------------------------------------------------------------------------

  private <T> WebTarget getTarget(Class<T> type, String objectId, String action, boolean secure) {
    // todo: Cache targets?
    WebTarget target = restClient.target(cfg.getBaseUrl()).path(pathResolver.resolve(type, '/'));
    if (objectId != null) {
      target = target.path(objectId);
    }
    if (action != null) {
      target = target.path(action);
    }

    if (secure) {
      target.register(loginFilter);
    }
    return target;
  }

  private <T> T readObject(Response response, GenericType<T> genericType) {
    try {
      return response.readEntity(genericType);
    } catch (Exception e) {
      throw handleException(response, e);
    }
  }

  private <T> ObjectList<T> readObjects(Response response, GenericType<ObjectList<T>> genericType) {
    try {
      return response.readEntity(genericType);
    } catch (Exception e) {
      throw handleException(response, e);
    }
  }

  private RuntimeException handleException(Response response, Exception cause) {
    if (cause instanceof ProcessingException) {
      String s = null;
      try {
        s = response.readEntity(String.class);
        return new SecuException("Unexpected Response: " + s);
      } catch (Exception e) {
        cause = e;
      }
    }
    return new SecuException("Error reading response.", cause);
  }


  private <T> Response executeRequest(WebTarget target, String method, T object) {
    Response response;
    try {
      response = target.request(MediaType.APPLICATION_JSON).method(method, Entity.json(object));
      response.bufferEntity();
    } catch (Exception e) {
      throw new SecuException("Error executing request", e);
    }
    return response;
  }

  private void handleResponseNot(Response response, Response.Status... stats) {
    for (Response.Status stat : stats) {
      if (response.getStatus() == stat.getStatusCode()) {
        return;
      }
    }
    Status status = null;
    String error = null;
    Exception ex = null;
    try {
      status = response.readEntity(Status.class);
      error = response.readEntity(String.class);
    } catch (Exception e) {
      ex = e;
    }

    if (status != null || error != null) {
      throw new SecuException("Error happened: " + (status == null ? error : status.getError() + ", "
          + status.getErrorDetails()));
    }

    throw new SecuException("Error happened." + ex);
  }


  interface GenericTypeResolver {
    <T> GenericType<ObjectList<T>> getGenericType(Class<T> type);
  }

}
