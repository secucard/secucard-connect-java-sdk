package com.secucard.connect.channel.rest;

import android.content.Context;
import com.android.volley.*;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.type.TypeReference;
import com.secucard.connect.Callback;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.QueryParams;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.transport.Status;
import com.secucard.connect.util.jackson.DynamicTypeReference;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Rest channel impl. for Android usage. Utilizes com.android.volley.
 */
public class VolleyChannel extends RestChannelBase {
  protected final android.content.Context context;
  protected RequestQueue requestQueue;
  private int requestTimeoutSec = 10;

  public VolleyChannel(String id, Context context, Configuration configuration) {
    super(configuration, id);
    this.context = context;
  }

  @Override
  public synchronized void open() {
    if (requestQueue == null) {
      requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }
  }

  @Override
  public synchronized void close() {
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
  public <T> T get(Class<T> type, String objectId, Callback<T> callback) {
    String url = buildRequestUrl(type, objectId);
    Request<T> request = buildRequest(Request.Method.GET, url, null, new DynamicTypeReference(type), callback);
    putToQueue(request);
    return null;
  }

  @Override
  public <T> ObjectList<T> getList(Class<T> type, QueryParams queryParams, final Callback<ObjectList<T>> callback) {
    String url = buildRequestUrl(type) + "?" + encodeQueryParams(queryParams);
    Request<ObjectList<T>> request = buildRequest(Request.Method.GET, url, null,
        new DynamicTypeReference(ObjectList.class, type), callback);
    putToQueue(request);
    return null;
  }

  @Override
  public <T> T create(T object, Callback<T> callback) {
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
  public <T extends SecuObject> T update(T object, Callback<T> callback) {
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
  public <T> T update(Class product, String objectId, String action, String actionArg, Object arg,
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
  public void delete(Class product, String objectId, String action, String actionArg, Callback callback) {
    String url = buildRequestUrl(product, objectId, action, actionArg);
    Request request = buildRequest(Request.Method.DELETE, url, null, new DynamicTypeReference(product), callback);
    putToQueue(request);
  }

  @Override
  public void delete(Class type, String objectId, Callback callback) {
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
      return future.get(requestTimeoutSec, TimeUnit.SECONDS);
    } catch (Exception e) {
      Throwable throwable = translate(e, ignoredState);
      if (throwable != null) {
        throw new RuntimeException(throwable);
      }
    }

    return null;
  }

  @Override
  public InputStream getStream(String url, Map<String, Object> parameters, final Map<String, String> headers,
                               final Callback<InputStream> callback) {
    throw new UnsupportedOperationException("Method not supported for Volley channel");
  }

  private String buildRequestUrl(Class type, String... pathArgs) {
    //todo: add backslash check
    String url = configuration.baseUrl;
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
    Map<String, String> headers = new HashMap<>();
    setAuthorizationHeader(headers);
    return new ObjectJsonRequest<>(method, url, requestBody, headers.size() == 0 ? null : headers, typeReference,
        callback);
  }

  private synchronized <T> Request<T> putToQueue(Request<T> request) {
    open();
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
          callback.completed(response);
        }
      }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
          callback.failed(translate(error));
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

  protected Throwable translate(Throwable throwable, Integer... ignoredStates) {
    RuntimeException ex;

    VolleyError error = null;
    if (throwable instanceof VolleyError) {
      error = (VolleyError) throwable;
    } else if (throwable.getCause() instanceof VolleyError) {
      error = (VolleyError) throwable.getCause();
    }

    if (error != null) {
      if (error.networkResponse == null) {
        ex = new RuntimeException("Error processing request.", error);
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
      return ex;
    }

    return throwable;
  }
}
