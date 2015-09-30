/*
 * Copyright (c) 2015. hp.weber GmbH & Co secucard KG (www.secucard.com)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.secucard.connect.net.rest;


import android.content.Context;
import com.android.volley.*;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.type.TypeReference;
import com.secucard.connect.client.Callback;
import com.secucard.connect.client.ClientContext;
import com.secucard.connect.client.ClientError;
import com.secucard.connect.net.ServerErrorException;
import com.secucard.connect.net.util.jackson.DynamicTypeReference;
import com.secucard.connect.product.common.model.ObjectList;
import com.secucard.connect.product.common.model.Status;
import com.secucard.connect.util.ExceptionMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Rest channel impl. for Android usage. Utilizes com.android.volley.
 */
public class VolleyChannel extends RestChannel {
  protected RequestQueue requestQueue;


  public VolleyChannel(Configuration configuration, ClientContext context) {
    super(configuration, context);
  }

  @Override
  public synchronized void open() {
    if (requestQueue == null) {
      requestQueue = Volley.newRequestQueue(((Context) context.runtimeContext).getApplicationContext());
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
  public <T> T request(Method method, Params params, Callback<T> callback) {
    String url = buildRequestUrl(params);

    RequestFuture<T> future = null;
    if (callback == null) {
      future = RequestFuture.newFuture();
    }

    Request<T> request = putToQueue(buildRequest(method, url, params,
        new DynamicTypeReference(params.returnType), callback, future));

    if (callback != null) {
      // done, callback will receive results
      return null;
    }

    future.setRequest(request);

    try {
      return future.get(configuration.responseTimeoutSec, TimeUnit.SECONDS);
    } catch (Throwable e) {
      throw translate(e);
    }
  }

  @Override
  public <T> ObjectList<T> requestList(Method method, Params params, Callback<ObjectList<T>> callback) {
    String url = buildRequestUrl(params);

    RequestFuture<ObjectList<T>> future = null;
    if (callback == null) {
      future = RequestFuture.newFuture();
    }

    Request<ObjectList<T>> request = putToQueue(buildRequest(method, url, params,
        new DynamicTypeReference(ObjectList.class, params.returnType), callback, future));

    if (callback != null) {
      // done, callback will receive results
      return null;
    }

    future.setRequest(request);

    try {
      return future.get(configuration.responseTimeoutSec, TimeUnit.SECONDS);
    } catch (Throwable e) {
      throw translate(e);
    }
  }


  @Override
  public <T, E> T post(String url, Map<String, Object> parameters, Map<String, String> headers, Class<T> responseType,
                       Class<E> errorResponseType) throws HttpErrorException {
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
//    putToQueue(request);

    try {
      return future.get(configuration.responseTimeoutSec, TimeUnit.SECONDS);
    } catch (Throwable throwable) {
      if (throwable.getCause() instanceof VolleyError) {
        VolleyError error = (VolleyError) throwable.getCause();
        if (error.networkResponse != null) {
          Object entity = null;
          if (error.networkResponse.data != null && errorResponseType != null) {
            try {
              entity = context.jsonMapper.map(new String(error.networkResponse.data), errorResponseType);
            } catch (IOException e) {
              entity = error.networkResponse.data;
            }
          }
          throw new HttpErrorException(error.networkResponse.statusCode, entity);
        }
      }

      throw translate(throwable);
    }
  }

  @Override
  public InputStream getStream(String url, Map<String, Object> parameters, final Map<String, String> headers,
                               final Callback<InputStream> callback) {
    throw new UnsupportedOperationException("Method not supported for Volley channel");
  }

  private String buildRequestUrl(Params params) {
    String url = configuration.baseUrl;

    if (!url.endsWith("/")) {
      url += "/";
    }

    if (params.appId != null) {
      url += "General/Apps/" + params.appId + "/callBackend";
    } else if (params.object != null) {
      url += buildTarget(params.object, '/');
    } else {
      throw new IllegalArgumentException("Missing object spec or app id.");
    }

    if (params.objectId != null) {
      url += "/" + params.objectId;
    }

    if (params.action != null) {
      url += "/" + params.action;
    }

    if (params.actionArg != null) {
      url += "/" + params.actionArg;
    }

    if (params.queryParams != null) {
      url += "?" + encodeQueryParams(params.queryParams);
    }

    return url;
  }

  private <T> ObjectJsonRequest<T> buildRequest(Method method, String url, Params params,
                                                TypeReference typeReference, final Callback<T> callback,
                                                RequestFuture<T> future) {
    String requestBody = null;

    if (params.data != null) {
      try {
        requestBody = context.jsonMapper.map(params.data);
      } catch (Exception e) {
        throw new ClientError("Error mapping request data to JSON", e);
      }
    }

    int m;
    if (method == Method.GET) {
      m = Request.Method.GET;
    } else if (method == Method.CREATE || method == Method.EXECUTE) {
      m = Request.Method.POST;
    } else if (method == Method.UPDATE) {
      m = Request.Method.PUT;
    } else if (method == Method.DELETE) {
      m = Request.Method.DELETE;
    } else {
      throw new IllegalArgumentException("Invalid method arg");
    }

    Map<String, String> headers = new HashMap<>();
    if (!params.options.anonymous) {
      setAuthorizationHeader(headers);
    }

    Response.Listener<T> listener;
    Response.ErrorListener errorListener;
    if (callback != null) {
      errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
          callback.failed(translate(error));
        }
      };
      listener = new Response.Listener<T>() {
        @Override
        public void onResponse(T response) {
          callback.completed(response);
        }
      };
    } else if (future != null) {
      listener = future;
      errorListener = future;
    } else {
      throw new IllegalStateException("Missing callback or future for request");
    }

    ObjectJsonRequest<T> request = new ObjectJsonRequest<>(m, url, requestBody, headers.size() == 0 ? null : headers,
        typeReference, listener, errorListener);

    return request;
  }

  private synchronized <T> Request<T> putToQueue(Request<T> request) {
    open();

    // looks like volley is using this timeout for connection and read timeout, at least the default HTTP stack (Hurl) does.
    request.setRetryPolicy(new DefaultRetryPolicy(configuration.connectTimeoutSec * 1000, 1, 1f));

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

    @Override
    public RetryPolicy getRetryPolicy() {
      return super.getRetryPolicy();
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
      try {
        String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers)).trim();
        T result = context.jsonMapper.map(jsonString, typeReference);
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

  @Override
  protected RuntimeException translate(Throwable throwable) {
    RuntimeException exception = super.translate(throwable);
    if (exception != null) {
      return exception;
    }

    VolleyError error = ExceptionMapper.unwrap(throwable, VolleyError.class);
    if (error != null && error.networkResponse != null) {
      if (error.networkResponse.data != null) {
        // this could be an specific secucard server error
        try {
          Status status = context.jsonMapper.map(new String(error.networkResponse.data), Status.class);
          return new ServerErrorException(status);
        } catch (Exception e) {
          // ignore
        }

        return new ClientError("Request failed with HTTP error " + error.networkResponse.statusCode, throwable);
      }
    }

    // just wrap in any runtime ex
    return new ClientError("Error executing request.", throwable);
  }
}
