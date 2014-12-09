package com.secucard.connect.channel.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.secucard.connect.Callback;
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
import com.secucard.connect.storage.DataStorage;
import com.secucard.connect.util.jackson.DynamicTypeReference;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public class RestChannel extends AbstractChannel implements AuthProvider {
  protected javax.ws.rs.client.Client restClient;
  protected LoginFilter loginFilter;
  protected final Configuration cfg;
  private DataStorage storage;
  private String id;
  private final boolean secure = false;
  protected UserAgentProvider userAgentProvider;
  private ObjectMapper jsonMapper = new ObjectMapper();

  public RestChannel(String id, Configuration cfg) {
    this.cfg = cfg;
    loginFilter = new LoginFilter(this);
    this.id = id;
  }

  public void setStorage(DataStorage storage) {
    this.storage = storage;
  }

  public void setUserAgentProvider(UserAgentProvider userAgentProvider) {
    this.userAgentProvider = userAgentProvider;
  }

  public void setJsonMapper(ObjectMapper jsonMapper) {
    this.jsonMapper = jsonMapper;
  }

  @Override
  public void open(Callback callback) {
    try {
      // rest client should be initialized just on time, client is expensive
      initClient();
      onCompleted(callback, null);
    } catch (Throwable e) {
      if (callback == null) {
        throw e;
      }
      onFailed(callback, e);
    }
  }

  private void initClient() {
    restClient = ClientBuilder.newClient();
  }

  @Override
  public void setEventListener(EventListener listener) {
    throw new UnsupportedOperationException("Rest channel doesn't support listener.");
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
    Map<String, String> parameters = createAuthParams(clientCredentials, userCredentials, refreshToken);

    Invocation.Builder builder = restClient.target(cfg.getOauthUrl()).request(MediaType.APPLICATION_FORM_URLENCODED);
    builder.header(HttpHeaders.USER_AGENT, userAgentProvider.getValue());
    Invocation invocation = builder.buildPost(Entity.form(new MultivaluedHashMap<>(parameters)));

    return getResponse(invocation, new DynamicTypeReference(Token.class), null);
  }

  @Override
  public String invoke(String command, Callback<String> callback) {
    Invocation invocation = builder(null, null, secure, command).buildGet();
    return getResponse(invocation, new DynamicTypeReference(String.class), callback);
  }

  @Override
  public <T> T getObject(Class<T> type, String objectId, Callback<T> callback) {
    Invocation invocation = builder(type, null, secure, objectId).buildGet();
    return getResponse(invocation, new DynamicTypeReference(type), callback);
  }

  @Override
  public <T> ObjectList<T> findObjects(Class<T> type, QueryParams queryParams, Callback<ObjectList<T>> callback) {
    Invocation invocation = builder(type, queryParamsToMap(queryParams), secure).buildGet();
    return getResponse(invocation, new DynamicTypeReference(ObjectList.class, type), callback,
        Response.Status.NOT_FOUND.getStatusCode());
  }

  @Override
  public <T extends SecuObject> T saveObject(T object, Callback<T> callback) {
    Entity<T> entity = Entity.json(object);
    Invocation.Builder builder = builder(object.getClass(), null, secure, object.getId());
    Invocation invocation = object.getId() == null ? builder.buildPost(entity) : builder.buildPut(entity);
    return getResponse(invocation, new DynamicTypeReference(object.getClass()), callback);
  }

  @Override
  public void deleteObject(Class type, String objectId, Callback<?> callback) {
    Invocation invocation = builder(type, null, secure, objectId).buildDelete();
    getResponse(invocation, null, callback);
  }

  @Override
  public <T> T execute(String action, String resourceId, String strArg, Object arg, Class<T> returnType, Callback<T> callback) {
    Entity entity = Entity.json(arg);
    Invocation invocation = builder(arg.getClass(), null, secure, resourceId, action, strArg).buildPost(entity);
    return getResponse(invocation, new DynamicTypeReference(returnType), callback);
  }


  @Override
  public void close(Callback callback) {
    try {
      restClient.close();
      onCompleted(callback, null);
    } catch (Throwable e) {
      if (callback == null) {
        throw e;
      }
      onFailed(callback, e);
    }
  }

  // private -------------------------------------------------------------------------------------------------------------------

  private MultivaluedHashMap<String, String> queryParamsToMap(QueryParams queryParams) {
    MultivaluedHashMap<String, String> map = new MultivaluedHashMap<>();

    boolean scroll = queryParams.getScrollId() != null && queryParams.getScrollId() > 0;
    if (scroll) {
      map.putSingle("scroll_id", queryParams.getScrollId().toString());
    }

    boolean scrollExpire = StringUtils.isNotBlank(queryParams.getScrollExpire());
    if (scrollExpire) {
      map.putSingle("scroll_expire", queryParams.getScrollExpire());
    }

    if (!scroll && queryParams.getCount() != null && queryParams.getCount() >= 0) {
      map.putSingle("count", queryParams.getCount().toString());
    }

    if (!scroll && !scrollExpire && queryParams.getOffset() != null && queryParams.getOffset() > 0) {
      map.putSingle("offset", queryParams.getOffset().toString());
    }

    List<String> fields = queryParams.getFields();
    if (!scroll && fields != null && fields.size() > 0) {
      // add "," separated list of field names
      String names = null;
      for (String field : fields) {
        names = names == null ? "" : names + ',';
        names += field;
      }
      map.putSingle("fields", names);
    }

    Map<String, String> sortOrder = queryParams.getSortOrder();
    if (!scroll && sortOrder != null) {
      for (Map.Entry<String, String> entry : sortOrder.entrySet()) {
        map.putSingle("sort[" + entry.getKey() + "]", entry.getValue());
      }
    }

    if (StringUtils.isNotBlank(queryParams.getQuery())) {
      map.putSingle("q", queryParams.getQuery());
    }

    return map;
  }

  private <T> Invocation.Builder builder(Class<T> type, MultivaluedMap<String, String> queryParams, boolean secure,
                                         String... pathArgs) {
    // todo: Cache targets?
    WebTarget target = restClient.target(cfg.getBaseUrl());

    if (type != null) {
      target = target.path(pathResolver.resolve(type, '/'));
    }

    for (String path : pathArgs) {
      target = target.path(path);
    }

    if (queryParams != null) {
      for (Map.Entry<String, List<String>> entry : queryParams.entrySet()) {
        target = target.queryParam(entry.getKey(), entry.getValue());
      }
    }

    if (secure) {
      target.register(loginFilter);
    }

    return target.request(MediaType.APPLICATION_JSON);
  }

  private <T> T getResponse(Invocation invocation, final TypeReference entityType, final Callback<T> callback,
                            final Integer... ignoredStatus) {
    T result = null;
    if (callback == null) {
      Future<Response> future = invocation.submit();
      try {
        result = readEntity(future.get(), entityType, ignoredStatus);
      } catch (Exception e) {
        throw translate(e);
      }
    } else {
      invocation.submit(
          new InvocationCallback<Response>() {
            @Override
            public void completed(Response response) {
              T result = null;
              try {
                result = readEntity(response, entityType);
              } catch (Exception e) {
                failed(e);
                return;
              }
              onCompleted(callback, result);
            }

            @Override
            public void failed(Throwable throwable) {
              onFailed(callback, throwable);
            }
          });
    }
    return result;
  }

  private <T> T readEntity(Response response, TypeReference entityType, Integer... ignoredStatus) throws IOException {
    for (Integer st : ignoredStatus) {
      if (response.getStatus() == st) {
        // ignore exception and return null
        return null;
      }
    }

    if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
      throw new WebApplicationException(response);
    }

    T result = null;
    if (entityType != null) {
      String json = response.readEntity(String.class);
      if (json != null) {
        result = jsonMapper.readValue(json, entityType);
      }
    }
    return result;
  }


  private SecuException translate(Throwable throwable) {
    return new SecuException(throwable);
  }

  static interface GenericTypeResolver {
    <T> GenericType<ObjectList<T>> getGenericType(Class<T> type);
  }
}
