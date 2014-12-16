package com.secucard.connect.channel.rest;

import android.content.Context;
import com.android.volley.*;
import com.android.volley.toolbox.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.secucard.connect.Callback;
import com.secucard.connect.auth.AuthProvider;
import com.secucard.connect.auth.OAuthClientCredentials;
import com.secucard.connect.auth.OAuthUserCredentials;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.auth.Token;
import com.secucard.connect.model.transport.QueryParams;
import com.secucard.connect.util.jackson.DynamicTypeReference;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Rest channel impl. for Android usage. Utilizes com.android.volley.
 */
public class VolleyChannel extends RestChannelBase implements AuthProvider {
  private RequestQueue requestQueue;
  private final android.content.Context context;

  public VolleyChannel(String id, Context context, Configuration configuration) {
    super(configuration, id);
    this.context = context;
  }

  @Override
  public void open(Callback callback) throws IOException {
    requestQueue = Volley.newRequestQueue(context.getApplicationContext(), new HurlStack());
  }

  @Override
  public void close(Callback callback) {
    requestQueue.stop();
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
        queryParamsToMap(queryParams), true, new DynamicTypeReference(ObjectList.class, type));
    requestQueue.add(request);
    return null;
  }

  @Override
  public <R, T extends SecuObject> R saveObject(T object, Callback<R> callback, Class<R> returnType) {
    String objectId = object.getId();
    String url = buildRequestUrl(object.getClass(), objectId);
    String requestBody;
    try {
      requestBody = jsonMapper.map(object);
    } catch (Exception e) {
      callback.failed(e);
      return null;
    }
    int method = objectId == null ? Request.Method.POST : Request.Method.PUT;
    DynamicTypeReference typeReference = new DynamicTypeReference(returnType == null ? object.getClass() : returnType);
    Request request = new ObjectJsonRequest<>(method, url, requestBody, callback, null, true, typeReference);
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
      requestBody = jsonMapper.map(arg);
    } catch (Exception e) {
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
    // todo: adapt the flow, it's the flow from java client, also not sure what the device string is or where userCredentials come from

    String device = "";
    Token token = (Token) storage.get("token" + id);
    Long expireTime = (Long) storage.get("expireTime" + id);
    if (token == null) {
      token = createToken(configuration.getClientCredentials(), null, null, device);
    } else {
      int expireTimeoutMs = 30 * 1000; // todo move to config
      if (expireTime != null && expireTime < System.currentTimeMillis() - expireTimeoutMs) {
        token = createToken(configuration.getClientCredentials(), null, token.getRefreshToken(), device);
      }
    }
    expireTime = System.currentTimeMillis() + token.getExpiresIn() * 1000;
    storage.save("token" + id, token);
    storage.save("expireTime" + id, expireTime);
    return null;
  }

  private Token createToken(OAuthClientCredentials clientCredentials, OAuthUserCredentials userCredentials,
                            String refreshToken, String device) {
    Map<String, String> authParams = createAuthParams(clientCredentials, userCredentials, refreshToken);
    authParams.put("device", device);

    RequestFuture<Token> future = RequestFuture.newFuture();
    Request<Token> request = new ObjectJsonRequest<>(Request.Method.POST, configuration.getOauthUrl(), null,
        future, future, authParams, false, new DynamicTypeReference(Token.class));
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
      url += "/" + pathResolver.resolveType(type, '/');
    }
    for (String pathArg : pathArgs) {
      url += "/" + pathArg;
    }
    return url;
  }

  /**
   * Request which maps JSON response strings directly into Java objects.
   * Jackson ObjectMapper will be used for this.
   *
   * @param <T> The actual response object type.
   */
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
        T result = jsonMapper.map(jsonString, typeReference);
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

    @Override
    public String getBodyContentType() {
      if (queryParams == null) {
        return super.getBodyContentType();
      }

      return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
    }

    @Override
    public byte[] getBody() {
      if (queryParams == null) {
        return super.getBody();
      }

      if (queryParams.size() > 0) {
        StringBuilder encodedParams = new StringBuilder();
        String paramsEncoding = getParamsEncoding();
        try {
          for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
            encodedParams.append('=');
            encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
            encodedParams.append('&');
          }
          return encodedParams.toString().getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException uee) {
          throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
      }
      return null;
    }
  }


}
