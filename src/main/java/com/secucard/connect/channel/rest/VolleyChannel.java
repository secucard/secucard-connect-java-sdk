package com.secucard.connect.channel.rest;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.type.TypeReference;
import com.secucard.connect.Callback;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.QueryParams;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.auth.Token;
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
    protected final boolean secure = true;
    protected final android.content.Context context;
    protected RequestQueue requestQueue;

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
    public <T> T getObject(Class<T> type, String objectId, Callback<T> callback) {
        String url = buildRequestUrl(type, objectId);
        Request<T> request = buildRequest(Request.Method.GET, url, null, new DynamicTypeReference(type), callback);
        requestQueue.add(request);
        return null;
    }


    @Override
    public <T> ObjectList<T> findObjects(Class<T> type, QueryParams queryParams, final Callback<ObjectList<T>> callback) {
        String url = buildRequestUrl(type) + "?" + encodeQueryParams(queryParams);
        Request<ObjectList<T>> request = buildRequest(Request.Method.GET, url, null,
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
        Request request = buildRequest(Request.Method.POST, url, requestBody, new DynamicTypeReference(object.getClass()),
                callback);
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
        Request request = buildRequest(Request.Method.PUT, url, requestBody, new DynamicTypeReference(object.getClass()),
                callback);
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
        Request request = buildRequest(Request.Method.PUT, url, requestBody, new DynamicTypeReference(returnType), callback);
        requestQueue.add(request);
        return null;
    }

    @Override
    public void deleteObject(Class product, String objectId, String action, String actionArg, Callback<?> callback) {
        String url = buildRequestUrl(product, objectId, action, actionArg);
        Request request = buildRequest(Request.Method.DELETE, url, null, new DynamicTypeReference(product), callback);
        requestQueue.add(request);
    }

    @Override
    public void deleteObject(Class type, String objectId, Callback<?> callback) {
        String url = buildRequestUrl(type, objectId);
        Request request = buildRequest(Request.Method.DELETE, url, null, new DynamicTypeReference(type), callback);
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
        Request request = buildRequest(Request.Method.POST, url, requestBody, new DynamicTypeReference(returnType),
                callback);
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
        Request request = buildRequest(Request.Method.POST, url, requestBody, new DynamicTypeReference(returnType),
                callback);
        requestQueue.add(request);
        return null;
    }


    @Override
    public <T> T post(String url, Map<String, Object> parameters, Map<String, String> headers, Class<T> responseType,
                      Integer... ignoredState) {
        RequestFuture future = RequestFuture.newFuture();
        String requestBody = encodeQueryParams(parameters);
        ObjectJsonRequest<Token> request = new ObjectJsonRequest<Token>(Request.Method.POST, url, requestBody, headers,
                new DynamicTypeReference(responseType), future, future) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
            }
        };


        try {
            //TODO: Moved here to prevent NPE app break
            future.setRequest(requestQueue.add(request));

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

    private <T> ObjectJsonRequest<T> buildRequest(int method, String url, String requestBody, TypeReference typeReference,
                                                  Callback<T> callback) {
        Map<String, String> headers = null;
        if (secure) {
            headers = new HashMap<>();
            setAuthorizationHeader(headers);
        }
        return new ObjectJsonRequest<>(method, url, requestBody, headers, typeReference, callback);
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
                    callback.failed(error);
                }
            });
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
