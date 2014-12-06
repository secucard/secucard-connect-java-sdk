package com.secucard.connect.channel.rest;

import android.content.Context;
import com.android.volley.*;
import com.android.volley.toolbox.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.secucard.connect.Callback;
import com.secucard.connect.auth.AuthProvider;
import com.secucard.connect.auth.OAuthClientCredentials;
import com.secucard.connect.channel.AbstractChannel;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.auth.Token;
import com.secucard.connect.model.transport.QueryParams;
import com.secucard.connect.util.jackson.DynamicTypeReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class VolleyChannel extends AbstractChannel implements AuthProvider {
  private RequestQueue requestQueue;
  private final android.content.Context context;
  protected final Configuration configuration;
  private ObjectMapper objectMapper = new ObjectMapper();

  public VolleyChannel(Context context, Configuration configuration) {
    this.context = context;
    this.configuration = configuration;
  }

  @Override
  public void open(Callback callback) throws IOException {
    requestQueue = Volley.newRequestQueue(context.getApplicationContext());
  }

  @Override
  public void close(Callback callback) {
    requestQueue.stop();
  }

  @Override
  public void setEventListener(EventListener listener) {
    throw new UnsupportedOperationException("Rest channel doesn't support listener.");
  }

  @Override
  public String invoke(String command, final Callback<String> callback) {
    String url = buildRequestUrl(null, command);
    Request<String> request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
      @Override
      public void onResponse(String response) {
        callback.completed(response);
      }
    }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        callback.failed(error);
      }
    });
    requestQueue.add(request);
    return null;
  }

  @Override
  public <T> T getObject(Class<T> type, String objectId, Callback<T> callback) {
    String url = buildRequestUrl(type, objectId);
    Request<T> request = new ObjectJsonRequest<>(Request.Method.GET, url, null, callback, null, true,
        new DynamicTypeReference(type));
    requestQueue.add(request);
    return null;
  }

  @Override
  public <T> ObjectList<T> findObjects(Class<T> type, QueryParams queryParams, final Callback<ObjectList<T>> callback) {
    String url = buildRequestUrl(type);
    Request<ObjectList<T>> request = new ObjectJsonRequest<>(Request.Method.GET, url, null, callback,
        getParameterMap(queryParams), true, new DynamicTypeReference(ObjectList.class, type));
    requestQueue.add(request);
    return null;
  }

  @Override
  public <T extends SecuObject> T saveObject(T object, Callback<T> callback) {
    String objectId = object.getId();
    String url = buildRequestUrl(object.getClass(), objectId);
    String requestBody;
    try {
      requestBody = objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      callback.failed(e);
      return null;
    }
    int method = objectId == null ? Request.Method.POST : Request.Method.PUT;
    Request<T> request = new ObjectJsonRequest<>(method, url, requestBody, callback, null, true,
        new DynamicTypeReference(object.getClass()));
    requestQueue.add(request);
    return null;
  }

  @Override
  public void deleteObject(Class type, String objectId, Callback<?> callback) {
    String url = buildRequestUrl(type, objectId);
    DynamicTypeReference typeReference = new DynamicTypeReference(type);
    Request request = new ObjectJsonRequest<>(Request.Method.DELETE, url, null, callback, null, true,
        typeReference);
    requestQueue.add(request);
  }

  @Override
  public <T> T execute(String action, String resourceId, String strArg, Object arg, Class<T> returnType,
                       Callback<T> callback) {
    String url = buildRequestUrl(arg.getClass(), resourceId, strArg);
    String requestBody;
    try {
      requestBody = objectMapper.writeValueAsString(arg);
    } catch (JsonProcessingException e) {
      callback.failed(e);
      return null;
    }
    Request<T> request = new ObjectJsonRequest<>(Request.Method.POST, url, requestBody, callback, null, true,
        new DynamicTypeReference(returnType));
    requestQueue.add(request);
    return null;
  }

  @Override
  public Token getToken() {
   /* Token token = storage.get("token" + id);
    Long expireTime = storage.get("expireTime" + id);
    if (token == null) {
      token = createToken(configuration.getClientCredentials(), null, null);
    } else if (expireTime != null && expireTime < System.currentTimeMillis() - 30 * 1000) {
      token = createToken(configuration.getClientCredentials(), null, token.getRefreshToken());
    }
    expireTime = System.currentTimeMillis() + token.getExpiresIn() * 1000;
    storage.save("token" + id, token);
    storage.save("expireTime" + id, expireTime);*/
    return null;
  }

  private Token createToken(OAuthClientCredentials clientCredentials) {
 /*   Map<String, String> params = new HashMap<String, String>();
    params.put("grant_type", GRANT_TYPE_APP_USER);
    params.put("username", clientCredentials.getClientId());
    params.put("password", password);
    params.put("device", device);
    params.put("client_id", clientCredentials.getClientId());
    params.put("client_secret", clientCredentials.getClientSecret());*/
    RequestFuture<Token> future = RequestFuture.newFuture();
    Request<Token> request = new ObjectJsonRequest<>(Request.Method.POST, configuration.getOauthUrl(), null, future, future,
        null, false, new DynamicTypeReference(Token.class));
    future.setRequest(requestQueue.add(request));

    try {
      return future.get();
    } catch (Exception e) {
      // todo: just log error
    }
    return null;
  }

  private String buildRequestUrl(Class type, String... pathArgs) {
    //todo: add backslash check
    String url = configuration.getBaseUrl();
    if (type != null) {
      url += "/" + pathResolver.resolve(type, '/');
    }
    for (String pathArg : pathArgs) {
      url += "/" + pathArg;
    }
    return url;
  }

  private Map<String, String> getParameterMap(QueryParams queryParams) {
    //todo: implement mapping
    return new HashMap<>();
  }

  private class ObjectJsonRequest<T> extends JsonRequest<T> {
    private TypeReference typeReference;
    private Map<String, String> queryParams;
    private Map<String, String> headers;

    public ObjectJsonRequest(int method, String url, String requestBody,
                             Response.Listener<T> listener, Response.ErrorListener errorListener,
                             Map<String, String> queryParams, boolean secure, TypeReference typeReference) {
      super(method, url, requestBody, listener, errorListener);
      this.typeReference = typeReference;
      this.queryParams = queryParams;
      if (secure) {
        headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + getToken());
      }
    }

    private ObjectJsonRequest(int method, String url, String requestBody, final Callback<T> callback,
                              Map<String, String> queryParams, boolean secure, TypeReference typeReference) {
      this(method, url, requestBody, new Response.Listener<T>() {
        @Override
        public void onResponse(T response) {
          callback.completed(response);
        }
      }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
          callback.failed(error);
        }
      }, queryParams, secure, typeReference);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
      try {
        String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        T result = objectMapper.readValue(jsonString, typeReference);
        return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
      } catch (Exception e) {
        return Response.error(new VolleyError(e));
      }
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
      return queryParams == null ? super.getParams() : queryParams;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
      return headers == null ? super.getHeaders() : headers;
    }
  }


}
