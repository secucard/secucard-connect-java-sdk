package com.secucard.connect.rest;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.secucard.connect.*;
import com.secucard.connect.auth.AuthProvider;
import com.secucard.connect.java.client.oauth.OAuthClientCredentials;
import com.secucard.connect.java.client.oauth.OAuthToken;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.Status;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.*;

public class RestChannel extends AbstractChannel implements AuthProvider {

  private final OAuthClientCredentials clientCredentials;
  private javax.ws.rs.client.Client client;
  private GenericTypeResolver typeResolver;
  private LoginFilter loginFilter;
  private OAuthToken token;
  private long expireTime;
  private final RestConfig cfg;

  public RestChannel(RestConfig cfg) {
    this.cfg = cfg;
    this.client = ClientBuilder.newClient();
    client.register(JacksonJsonProvider.class);
    loginFilter = new LoginFilter(this);
    clientCredentials = new OAuthClientCredentials("webapp",
        "821fc7042ec0ddf5cc70be9abaa5d6d311db04f4679ab56191038cb6f7f9cb7c");
  }

  public void setTypeResolver(GenericTypeResolver typeResolver) {
    this.typeResolver = typeResolver;
  }


  @Override
  public void open() {
  }

  @Override
  public synchronized OAuthToken getToken() {
    if (token == null) {
      token = createToken("sten@beispiel.net", "secrets", null);
    } else if (expireTime < System.currentTimeMillis() - 30 * 1000) {
      token = createToken(null, null, token.getRefreshToken());
    }
    this.expireTime = System.currentTimeMillis() + token.getExpiresIn() * 1000;
    return token;
  }

  private OAuthToken createToken(String username, String password, String refreshToken) {
    MultivaluedMap<String, String> parameters = new MultivaluedHashMap<>();
    parameters.add("client_id", clientCredentials.getClientId());
    parameters.add("client_secret", clientCredentials.getClientSecret());
    if (refreshToken != null) {
      parameters.add("grant_type", "refresh_token");
      parameters.add("refresh_token", refreshToken);
    } else {
      parameters.add("username", username);
      parameters.add("password", password);
    }
    WebTarget target = client.target(cfg.getOauthUrl());
    Response response = target.request(MediaType.APPLICATION_FORM_URLENCODED).post(Entity.form(parameters));
    return response.readEntity(OAuthToken.class);
  }

  @Override
  public <T extends SecuObject> ObjectList<T> findObjects(Class<T> type, QueryParams q) {
    WebTarget target = getTarget(type, null, null);
    Response response = executeRequest(target, "GET", null);

    handleResponseNot(response, Response.Status.NOT_FOUND, Response.Status.OK);
    if (Response.Status.NOT_FOUND.getStatusCode() == response.getStatus()) {
      return null;
    }

    return readObjects(response, typeResolver.getGenericType(type));
  }

  @Override
  public <T extends SecuObject> T getObject(Class<T> type, String objectId) {
    WebTarget target = getTarget(type, objectId, null);
    Response response = executeRequest(target, "GET", null);

    handleResponseNot(response, Response.Status.NOT_FOUND, Response.Status.OK);
    if (Response.Status.NOT_FOUND.getStatusCode() == response.getStatus()) {
      return null;
    }

    return readObject(response, new GenericType<T>(type));
  }

  @Override
  public <T extends SecuObject> T saveObject(T object) {
    WebTarget target = getTarget(object.getClass(), object.getId(), null);
    Response response = executeRequest(target, object.getId() == null ? "POST" : "PUT", object);
    handleResponseNot(response, Response.Status.OK);
    return readObject(response, new GenericType<T>(object.getClass()));
  }

  @Override
  public <T extends SecuObject> boolean deleteObject(Class<T> type, String objectId) {
    WebTarget target = getTarget(type, objectId, null);
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
    WebTarget target = getTarget(arg.getClass(), ids[0], action);
    Response response = executeRequest(target, "POST", null);

    handleResponseNot(response, Response.Status.NOT_FOUND, Response.Status.OK);
    if (Response.Status.NOT_FOUND.getStatusCode() == response.getStatus()) {
      return null;
    }

    return readObject(response, new GenericType<R>(returnType));
  }

  @Override
  public void close() {
    client.close();
  }

  // private -------------------------------------------------------------------------------------------------------------------

  private <T> WebTarget getTarget(Class<T> type, String objectId, String action) {
    // todo: Cache targets?
    WebTarget target = client.target(cfg.getBaseUrl()).path(pathResolver.resolve(type, '/'));
    if (objectId != null) {
      target = target.path(objectId);
    }
    if (action != null) {
      target = target.path(action);
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
