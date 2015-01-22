package com.secucard.connect.channel.rest;

import android.content.Context;
import android.util.Log;
import com.android.volley.*;
import com.android.volley.toolbox.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.secucard.connect.Callback;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.auth.Token;
import com.secucard.connect.model.transport.QueryParams;
import com.secucard.connect.util.jackson.DynamicTypeReference;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Rest channel impl. for Android usage. Utilizes com.android.volley.
 */
public class VolleyChannel extends RestChannelBase {
  private final boolean secure = true;
  private RequestQueue requestQueue;
  private final android.content.Context context;

  public VolleyChannel(String id, Context context, Configuration configuration) {
    super(configuration, id);
    this.context = context;
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
    Request<T> request = new ObjectJsonRequest<>(Request.Method.GET, url, null, secure, new DynamicTypeReference(type),
        callback);
    requestQueue.add(request);
    return null;
  }


  @Override
  public <T> ObjectList<T> findObjects(Class<T> type, QueryParams queryParams, final Callback<ObjectList<T>> callback) {
    String url = buildRequestUrl(type) + "?" + encodeQueryParams(queryParams);
    Request<ObjectList<T>> request = new ObjectJsonRequest<>(Request.Method.GET, url, null, secure,
        new DynamicTypeReference(ObjectList.class, type), callback);
    //        request.setTag(url);
    Log.d("ConnectJavaClient", "VolleyChannel: findObjects ->" + type);
    requestQueue.add(request);
    return null;
  }

  @Override
  public <T> T createObject(T object, Callback<T> callback) {
    String url = buildRequestUrl(object.getClass());
    String requestBody;
    try {
      requestBody = jsonMapper.map(object);
    } catch (Exception e) {
      callback.failed(e);
      return null;
    }
    Request request = new ObjectJsonRequest<>(Request.Method.POST, url, requestBody, secure,
        new DynamicTypeReference(object.getClass()), callback);
    requestQueue.add(request);
    return null;
  }

  @Override
  public <T extends SecuObject> T updateObject(T object, Callback<T> callback) {
    String url = buildRequestUrl(object.getClass(), object.getId());
    String requestBody;
    try {
      requestBody = jsonMapper.map(object);
    } catch (Exception e) {
      callback.failed(e);
      return null;
    }
    Request request = new ObjectJsonRequest<>(Request.Method.PUT, url, requestBody, secure,
        new DynamicTypeReference(object.getClass()), callback);
    requestQueue.add(request);
    return null;
  }

  @Override
  public <T> T updateObject(Class product, String objectId, String action, String actionArg, Object arg,
                            Class<T> returnType, Callback<T> callback) {
    String url = buildRequestUrl(product, objectId, action, actionArg);
    String requestBody;
    try {
      requestBody = jsonMapper.map(arg);
    } catch (Exception e) {
      callback.failed(e);
      return null;
    }
    Request request = new ObjectJsonRequest<>(Request.Method.PUT, url, requestBody, secure,
        new DynamicTypeReference(returnType), callback);
    requestQueue.add(request);
    return null;
  }

  @Override
  public void deleteObject(Class product, String objectId, String action, String actionArg, Callback<?> callback) {
    String url = buildRequestUrl(product, objectId, action, actionArg);
    DynamicTypeReference typeReference = new DynamicTypeReference(product);
    Request request = new ObjectJsonRequest<>(Request.Method.DELETE, url, null, secure, typeReference, callback);
    requestQueue.add(request);
  }

  @Override
  public void deleteObject(Class type, String objectId, Callback<?> callback) {
    String url = buildRequestUrl(type, objectId);
    Request request = new ObjectJsonRequest<>(Request.Method.DELETE, url, null, secure, new DynamicTypeReference(type),
        callback);
    requestQueue.add(request);
  }

  @Override
  public <T> T execute(Class product, String objectId, String action, String actionArg, Object arg, Class<T> returnType,
                       Callback<T> callback) {
    String url = buildRequestUrl(product, objectId, action, actionArg);
    String requestBody;
    try {
      requestBody = jsonMapper.map(arg);
    } catch (Exception e) {
      callback.failed(e);
      return null;
    }
    Request<T> request = new ObjectJsonRequest<>(Request.Method.POST, url, requestBody, secure,
        new DynamicTypeReference(returnType), callback);
    requestQueue.add(request);
    return null;
  }

  @Override
  public <T> T execute(String appId, String action, Object arg, Class<T> returnType, Callback<T> callback) {
    String url = buildRequestUrl(null, appId, action);
    String requestBody;
    try {
      requestBody = jsonMapper.map(arg);
    } catch (Exception e) {
      callback.failed(e);
      return null;
    }
    Request<T> request = new ObjectJsonRequest<>(Request.Method.POST, url, requestBody, secure,
        new DynamicTypeReference(returnType), callback);
    requestQueue.add(request);
    return null;
  }


  @Override
  public <T> T post(String url, Map<String, Object> parameters, Map<String, String> headers, Class<T> responseType,
                    Integer... ignoredState) {
    RequestFuture future = RequestFuture.newFuture();
    String requestBody = encodeQueryParams(parameters);
    ObjectJsonRequest<Token> request = new ObjectJsonRequest<Token>(Request.Method.POST, url, requestBody,
        new DynamicTypeReference(responseType), future, future) {
      @Override
      public String getBodyContentType() {
        return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
      }
    };

    request.setHeaders(headers);

    future.setRequest(requestQueue.add(request));

    try {
      return (T) future.get();
    } catch (Exception e) {
      e.printStackTrace();
      // todo: just log error
    }
    return null;
  }

  @Override
  public InputStream getStream(String url, Map<String, Object> parameters, Map<String, String> headers) {
    return null;
  }

  private String buildRequestUrl(Class type, String... pathArgs) {
    //todo: add backslash check
    String url = configuration.getBaseUrl();
    String str;
    if (type == null) {
      // first arg should be app id
      str = pathResolver.resolveAppId(pathArgs[0], '/');
      pathArgs = Arrays.copyOfRange(pathArgs, 1, pathArgs.length);
    } else {
      str = pathResolver.resolveType(type, '/');
    }
    url += "/" + str;

    for (String pathArg : pathArgs) {
      if (pathArg != null) {
        url += "/" + pathArg;
      }
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
    private Map<String, String> headers;

    public void setHeaders(Map<String, String> headers) {
      if (headers == null) {
        return;
      }
      if (this.headers == null) {
        this.headers = new HashMap<>();
      }
      this.headers.putAll(headers);
    }

    private ObjectJsonRequest(int method, String url, String requestBody, TypeReference typeReference,
                              Response.Listener<T> listener, Response.ErrorListener errorListener) {
      super(method, url, requestBody, listener, errorListener);
      this.typeReference = typeReference;
    }

    private ObjectJsonRequest(int method, String url, String requestBody,
                              boolean secure, TypeReference typeReference, final Callback<T> callback) {
      super(method, url, requestBody, new Response.Listener<T>() {
        @Override
        public void onResponse(T response) {
          callback.completed(response);
        }
      }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
          callback.failed(error);
        }
      });
      this.typeReference = typeReference;
      if (secure) {
        headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + authProvider.getToken().getAccessToken());
        // getBodyContentType() handles media type
      }
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
    public Map<String, String> getHeaders() throws AuthFailureError {
      return headers == null ? super.getHeaders() : headers;
    }
  }
}
