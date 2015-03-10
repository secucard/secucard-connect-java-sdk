package com.secucard.connect.channel.rest;

import android.content.Context;
import com.android.volley.*;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.type.TypeReference;
import com.secucard.connect.Callback;
import com.secucard.connect.SecuException;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.QueryParams;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.auth.Token;
import com.secucard.connect.model.transport.Status;
import com.secucard.connect.util.jackson.DynamicTypeReference;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Rest channel impl. for Android usage. Utilizes com.android.volley.
 */
public class VolleyChannel extends RestChannelBase {
  protected final android.content.Context context;
  protected RequestQueue requestQueue;

  public VolleyChannel(String id, Context context, Configuration configuration) {
    super(configuration, id);
    this.context = context;
  }

  @Override
  public synchronized void open(Callback callback) throws IOException {
    if (requestQueue == null) {
      requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }
  }

  @Override
  public synchronized void close(Callback callback) {
    if (requestQueue == null)
      return;

    requestQueue.cancelAll(new RequestQueue.RequestFilter() {
      @Override
      public boolean apply(Request<?> request) {
        return true;
      }
    });
    requestQueue.stop();
    requestQueue = null;
  }

  @Override
  public <T> T getObject(Class<T> type, String objectId, Callback<T> callback) {
    String url = buildRequestUrl(type, objectId);
    Request<T> request = buildRequest(Request.Method.GET, url, null, new DynamicTypeReference(type), callback);
    putToQueue(request);
    return null;
  }

  @Override
  public <T> ObjectList<T> findObjects(Class<T> type, QueryParams queryParams, final Callback<ObjectList<T>> callback) {
    String url = buildRequestUrl(type) + "?" + encodeQueryParams(queryParams);
    Request<ObjectList<T>> request = buildRequest(Request.Method.GET, url, null,
        new DynamicTypeReference(ObjectList.class, type), callback);
    putToQueue(request);
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
    Request request = buildRequest(Request.Method.POST, url, requestBody, new DynamicTypeReference(object.getClass()),
        callback);
    putToQueue(request);
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
    Request request = buildRequest(Request.Method.PUT, url, requestBody, new DynamicTypeReference(object.getClass()),
        callback);
    putToQueue(request);
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
    Request request = buildRequest(Request.Method.PUT, url, requestBody, new DynamicTypeReference(returnType), callback);
    putToQueue(request);
    return null;
  }

  @Override
  public void deleteObject(Class product, String objectId, String action, String actionArg, Callback<?> callback) {
    String url = buildRequestUrl(product, objectId, action, actionArg);
    Request request = buildRequest(Request.Method.DELETE, url, null, new DynamicTypeReference(product), callback);
    putToQueue(request);
  }

  @Override
  public void deleteObject(Class type, String objectId, Callback<?> callback) {
    String url = buildRequestUrl(type, objectId);
    Request request = buildRequest(Request.Method.DELETE, url, null, new DynamicTypeReference(type), callback);
    putToQueue(request);
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
    Request request = buildRequest(Request.Method.POST, url, requestBody, new DynamicTypeReference(returnType),
        callback);
    putToQueue(request);
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
    Request request = buildRequest(Request.Method.POST, url, requestBody, new DynamicTypeReference(returnType),
        callback);
    putToQueue(request);
    return null;
  }


  @Override
  public <T> T post(String url, Map<String, Object> parameters, Map<String, String> headers, Class<T> responseType,
                    Integer... ignoredState) {
    RequestFuture<T> future = RequestFuture.newFuture();
    String requestBody = encodeQueryParams(parameters);
    ObjectJsonRequest<T> request = new ObjectJsonRequest<T>(Request.Method.POST, url, requestBody, headers,
        new DynamicTypeReference(responseType), future, future) {

      @Override
      public String getBodyContentType() {
        return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
      }
    };

    future.setRequest(putToQueue(request));

    try {
      return future.get();
    } catch (Exception e) {
      RuntimeException exception = translate(e, ignoredState);
      if (exception != null) {
        throw exception;
      }
    }

    return null;
  }

  /**
   * Just "wraps" retrieved response content in a stream.
   * @param url
   * @param parameters
   * @param headers
   * @return
   */
  @Override
  public InputStream getStream(String url, Map<String, Object> parameters, final Map<String, String> headers) {
    final RequestFuture<InputStream> future = RequestFuture.newFuture();
    final String queryParams = encodeQueryParams(parameters);
    if (queryParams != null) {
      url += "?" + queryParams;
    }
    Request<InputStream> request = new Request<InputStream>(Request.Method.GET, url, future) {

      @Override
      protected Response<InputStream> parseNetworkResponse(NetworkResponse response) {
        InputStream inputStream = new ByteArrayInputStream(response.data);
        return Response.success(inputStream, HttpHeaderParser.parseCacheHeaders(response));
      }

      @Override
      protected void deliverResponse(InputStream response) {
        future.onResponse(response);
      }

      @Override
      public Map<String, String> getHeaders() throws AuthFailureError {
        return headers == null ? super.getHeaders() : headers;
      }
    };

    future.setRequest(putToQueue(request));

    try {
      return future.get();
    } catch (Exception e) {
      throw translate(e);
    }
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

  private <T> ObjectJsonRequest<T> buildRequest(int method, String url, String requestBody, TypeReference typeReference,
                                                Callback<T> callback) {
    Map<String, String> headers = null;
    if (secure) {
      Token token = authProvider.getToken();
      if (token != null) {
        headers = new HashMap<>();
        setAuthorizationHeader(headers, token.getAccessToken());
      }
    }
    return new ObjectJsonRequest<>(method, url, requestBody, headers, typeReference, callback);
  }

  private synchronized <T> Request<T> putToQueue(Request<T> request) {
    if (requestQueue == null) {
      try {
        open(null);
      } catch (IOException e) {
        throw new SecuException("Error creating request queue", e);
      }
    }
    return requestQueue.add(request);
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

    private ObjectJsonRequest(int method, String url, String requestBody, Map<String, String> headers,
                              TypeReference typeReference, Response.Listener<T> listener,
                              Response.ErrorListener errorListener) {
      super(method, url, requestBody, listener, errorListener);
      this.typeReference = typeReference;
      this.headers = headers;
    }

    private ObjectJsonRequest(int method, String url, String requestBody, Map<String, String> headers,
                              TypeReference typeReference, final Callback<T> callback) {
      this(method, url, requestBody, headers, typeReference, new Response.Listener<T>() {
        @Override
        public void onResponse(T response) {
          onCompleted(callback, response);
        }
      }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
          onFailed(callback, translate(error));
        }
      });
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
      try {
        String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers)).trim();
        T result = jsonMapper.map(jsonString, typeReference);
        return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
      } catch (Exception e) {
        return Response.error(new VolleyError("Error reading response data", e));
      }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
      return headers == null ? super.getHeaders() : headers;
    }
  }

  protected RuntimeException translate(Throwable throwable, Integer... ignoredStates) {
    RuntimeException ex;

    VolleyError error = null;
    if (throwable instanceof VolleyError) {
      error = (VolleyError) throwable;
    } else if (throwable.getCause() instanceof VolleyError) {
      error = (VolleyError) throwable.getCause();
    }

    if (error != null) {
      if (error.networkResponse == null) {
        ex = new SecuException("Error processing request.", error);
      } else {
        if (ignoredStates != null) {
          for (Integer state : ignoredStates) {
            if (state != null && state == error.networkResponse.statusCode) {
              return null;
            }
          }
        }
        Status status = null;
        if (error.networkResponse.data != null) {
          // this could be an specific secucard server error
          try {
            status = jsonMapper.map(new String(error.networkResponse.data), Status.class);
          } catch (Exception e) {
          }
        }
        if (status == null) {
          // no status info available, just a plain http error
          ex = new HttpErrorException(error.networkResponse.statusCode);
        } else {
          ex = translateError(status, error.getCause());
        }
      }
    } else {
      ex = new SecuException("Error processing request", throwable);
    }
    return ex;
  }
}
