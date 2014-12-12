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
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.secucard.connect.Callback;
import com.secucard.connect.auth.AuthProvider;
import com.secucard.connect.auth.OAuthClientCredentials;
import com.secucard.connect.auth.OAuthUserCredentials;
import com.secucard.connect.channel.AbstractChannel;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.auth.Token;
import com.secucard.connect.model.transport.QueryParams;
import com.secucard.connect.storage.DataStorage;
import com.secucard.connect.util.jackson.DynamicTypeReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Rest channel impl. for Android usage. Utilizes com.android.volley.
 */
public class VolleyChannel extends AbstractChannel implements AuthProvider {
    protected final Configuration configuration;
    private final android.content.Context context;
    private RequestQueue requestQueue;
    private String id;
    private ObjectMapper objectMapper = new ObjectMapper();
    private DataStorage storage;

    public VolleyChannel(String id, Context context, Configuration configuration) {
        this.id = id;
        this.context = context;
        this.configuration = configuration;
    }

    public void setStorage(DataStorage storage) {
        this.storage = storage;
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
        // todo: adapt the flow, it's the flow from java client, also not sure what the device string is or where userCredentials come from

        String device = "1";
        Token token = storage.get("token" + id);
        Long expireTime = storage.get("expireTime" + id);
        if (token == null) {
            token = createToken(configuration.getClientCredentials(), null, null, device);
        } else {
            int expireTimeoutMs = 30 * 1000; // todo move to config
            if (expireTime != null && expireTime < System.currentTimeMillis() - expireTimeoutMs) {
                token = createToken(configuration.getClientCredentials(), null, token.getRefreshToken(), device);
            }
        }
        if(token != null) {
            expireTime = System.currentTimeMillis() + token.getExpiresIn() * 1000;
            storage.save("token" + id, token);
            storage.save("expireTime" + id, expireTime);
        }
        return null;
    }

    private Token createToken(OAuthClientCredentials clientCredentials, OAuthUserCredentials userCredentials,
                              String refreshToken, String device) {
        userCredentials = new OAuthUserCredentials("checkout@secucard.com", "checkout");
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
            e.printStackTrace();
            Log.e("ConnectJavaClient", "VolleyChannel: " + e.getMessage());
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
            }else{
                headers = new HashMap<>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
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
//            if (headers == null) {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("Content-Type", "application/x-www-form-urlencoded");
//                return headers;
//            } else {
//                return super.getHeaders();
//            }
            return headers == null ? super.getHeaders() : headers;
        }
    }


}
