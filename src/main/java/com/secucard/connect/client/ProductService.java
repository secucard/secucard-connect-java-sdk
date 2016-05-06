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

package com.secucard.connect.client;

import com.secucard.connect.net.Channel;
import com.secucard.connect.net.Options;
import com.secucard.connect.product.common.model.*;
import com.secucard.connect.util.CallbackAdapter;
import com.secucard.connect.util.Converter;
import com.secucard.connect.util.ExceptionMapper;

import java.util.List;

/**
 * The base class of all product services.
 * <p/>
 * Please note each service method may throw just the following runtime exceptions:<br/>
 * - {@link com.secucard.connect.client.APIError} <br/>
 * - {@link com.secucard.connect.client.NetworkError} <br/>
 * - {@link com.secucard.connect.auth.exception.AuthFailedException} <br/>
 * - {@link com.secucard.connect.client.ClientError} <br/>
 * <p/>
 * These exceptions may be handled either separate on each call or by setting an global exception handler,
 * see {@link com.secucard.connect.SecucardConnect#setServiceExceptionHandler(com.secucard.connect.client.ExceptionHandler)}
 *
 * @param <T> The actual product resource type
 */
public abstract class ProductService<T extends SecuObject> {
  protected ClientContext context;

  public void setContext(ClientContext context) {
    this.context = context;
  }

  /**
   * Returns meta data associated with this product service.<br/>
   * When implementing this method it's a goo idea to return just a constant static instance instead creating new ones.<br/>
   * The convention is to have a field:<br/>
   * public static final ServiceMetaData<...> META_DATA; <br/>
   * This way the meta data is also accessibly statically.
   */
  public abstract ServiceMetaData<T> getMetaData();


  protected Options getDefaultOptions() {
    return Options.getDefault();
  }


  // get, get list -----------------------------------------------------------------------------------------------------

  public T get(String id) {
    return get(id, null);
  }

  public T get(String id, Callback<T> callback) {
    return get(id, null, callback);
  }

  protected T get(String id, Options options, Callback<T> callback) {
    return get(id, null, null, options, callback);
  }

  public T get(String id, String action, String actionArg) {
    return get(id, action, actionArg, null, null);
  }

  public T get(String id, String action, String actionArg, Callback<T> callback) {
    return get(id, action, actionArg, null, callback);
  }

  protected T get(String id, String action, String actionArg, Options options, Callback<T> callback) {
    return request(Channel.Method.GET,
        new Channel.Params(getObject(), id, action, actionArg, null, getResourceType(), options), options, callback);
  }

  public ObjectList<T> getList(QueryParams queryParams) {
    return getList(queryParams, null);
  }

  public ObjectList<T> getList(QueryParams queryParams, Callback<ObjectList<T>> callback) {
    return getList(queryParams, null, callback);
  }

  protected ObjectList<T> getList(QueryParams queryParams, Options options, Callback<ObjectList<T>> callback) {
    return requestList(Channel.Method.GET,
        new Channel.Params(getObject(), queryParams, getResourceType(), options), options, callback);
  }

  /**
   * Much like getList() but additionally supports fast (forward only) retrieval of large result data amounts by
   * returning the results in batches or pages, like forward scrolling through the whole result.<br/>
   * With a first call to this method you may specify all needed search params like count or sort. The returned
   * collection is the first batch of results and contains also an unique id usable to get the next batches by calling
   * getNextBatch() with this id.<br/>
   * Actually this API call will create a result set snapshot or search context (server side) from which the results
   * are returned. The mandatory parameter expireTime specifies how long this snapshot should exist on the server.
   * Since this allocates server resources please choose carefully appropriate times according to your needs,
   * otherwise the server resource monitoring may limit your requests.
   * <br/>
   * Note those methods exists just for convenience purposes. Since all this behaviour is fully controlled by
   * the {@link QueryParams} args of a call a normal getList call can do the same.
   *
   * @param queryParams The query params to apply.
   * @param expireTime  String specifying the expire time expression of the search context on the server. Valid
   *                    expression are "{number}{s|m|h}" like "5m" for 5 minutes.
   * @return A collection of result items.
   */
  public ObjectList<T> getScrollableList(QueryParams queryParams, String expireTime) {
    return getScrollableList(queryParams, expireTime, null);
  }

  /**
   * Like {@link #getScrollableList(QueryParams, String)} but with an additional callback.
   */
  public ObjectList<T> getScrollableList(QueryParams queryParams, String expireTime, Callback<ObjectList<T>> callback) {
    queryParams.setScrollExpire(expireTime);
    return getList(queryParams, callback);
  }

  /**
   * Returns the next batch of results initially requested by getScrollableList() call.
   *
   * @param id The id of the result set snapshot to access. Get this id from the collection returned by
   *           getScrollableList().
   * @return The collection of result items. The number of returned items may be less as requested
   * by the initial count parameter at the end of the result set. Has count of 0 if no data is available anymore.
   * The total count of items in result is not set here.
   * todo: what happens on expiring
   */
  public ObjectList<T> getNextBatch(String id) {
    return getNextBatch(id, null);
  }

  /**
   * Like {@link #getNextBatch(String)} but with an additional callback.
   */
  public ObjectList<T> getNextBatch(String id, Callback<ObjectList<T>> callback) {
    QueryParams qp = new QueryParams();
    qp.setScrollId(id);
    return getList(qp, callback);
  }

  /**
   * Like {@link #getSimpleList(com.secucard.connect.product.common.model.QueryParams, com.secucard.connect.client.Callback)}
   * but without callback.
   */
  public List<T> getSimpleList(QueryParams queryParams) {
    return getSimpleList(queryParams, null);
  }

  /**
   * Get simple list of product resources.
   *
   * @param queryParams Contains the query params to apply.
   * @param callback    Callback for getting the results asynchronous.
   * @return The resource objects. Null if nothing found.
   */
  public List<T> getSimpleList(QueryParams queryParams, Callback<List<T>> callback) {
    return getSimpleList(queryParams, null, callback);
  }

  protected List<T> getSimpleList(QueryParams queryParams, Options options, Callback<List<T>> callback) {
    Converter.ToListConverter<T> converter = new Converter.ToListConverter<>();
    CallbackAdapter<ObjectList<T>, List<T>> cb = callback == null ? null : new CallbackAdapter<>(callback, converter);
    ObjectList<T> list = requestList(Channel.Method.GET,
        new Channel.Params(getObject(), queryParams, getResourceType(), options), options, cb);
    return converter.convert(list);
  }


  // create ------------------------------------------------------------------------------------------------------------

  /**
   * Like {@link #create(com.secucard.connect.product.common.model.SecuObject, com.secucard.connect.client.Callback)}
   * but without callback.
   */
  public T create(T object) {
    return create(object, null);
  }

  /**
   * Creates a new product resource and returns the result. The server may add additional default data to it
   * so additional field may be filled in the result, like id. Use this result for further processing instead of the
   * provided.
   * <p/>
   * If the resource can't be created or another error happens ClientError will be thrown.
   * Inspect the code and userMessage field to get info about the error cause.
   *
   * @param object   The resource to create.
   * @param callback Callback receiving the result asynchronous.
   */
  public T create(T object, Callback<T> callback) {
    return create(object, null, callback);
  }

  protected T create(T object, Options options, Callback<T> callback) {
    return request(Channel.Method.CREATE,
        new Channel.Params(getObject(), object, object.getClass(), options), options, callback);
  }


  // update ------------------------------------------------------------------------------------------------------------

  /**
   * Like {@link #update(com.secucard.connect.product.common.model.SecuObject, com.secucard.connect.client.Callback)}
   * but without callback.
   */
  public T update(T object) {
    return update(object, null, null);
  }

  /**
   * Updates a product resource and returns the result. The server may add additional default data to it
   * so additional field may be filled in the result. Use this result for further processing instead of the
   * provided.
   * <p/>
   * If the resource can't be updated or another error happens ClientError will be thrown.
   * Inspect the code and userMessage field to get info about the error cause.
   *
   * @param object   The resource to update.
   * @param callback Callback receiving the result asynchronous.
   */
  public T update(T object, Callback<T> callback) {
    return update(object, null, callback);
  }

  protected T update(T object, Options options, Callback<T> callback) {
    return request(Channel.Method.UPDATE,
        new Channel.Params(getObject(), object.getId(), object, object.getClass(), options), options, callback);
  }

  protected <R> R update(String id, String action, String actionArg, Object object, Class<R> returnType,
                         Options options, Callback<R> callback) {
    return request(Channel.Method.UPDATE,
        new Channel.Params(getObject(), id, action, actionArg, object, returnType, options), options, callback);
  }

  protected Boolean updateToBool(String id, String action, String actionArg, Object object, Options options,
                                 Callback<Boolean> callback) {
    Converter<Result, Boolean> converter = Converter.RESULT2BOOL;
    CallbackAdapter<Result, Boolean> cb = callback == null ? null : new CallbackAdapter<>(callback, converter);
    Result result = request(Channel.Method.UPDATE,
        new Channel.Params(getObject(), id, action, actionArg, object, Result.class, options), options, cb);
    return converter.convert(result);
  }

  // delete ------------------------------------------------------------------------------------------------------------

  public void delete(String id) {
    delete(id, null, null);
  }

  public void delete(String id, Callback<Void> callback) {
    delete(id, null, callback);
  }

  protected void delete(String id, Options options, Callback<Void> callback) {
    request(Channel.Method.DELETE, new Channel.Params(getObject(), id, options), options, callback);
  }

  protected void delete(String id, String action, String actionArg, Options options, Callback<Void> callback) {
    request(Channel.Method.DELETE, new Channel.Params(getObject(), id, action, actionArg, options), options, callback);
  }

  // execute -----------------------------------------------------------------------------------------------------------

  protected <R> R execute(String id, String action, String actionArg, Object object, Class<R> returnType, Options options,
                          Callback<R> callback) {
    return request(Channel.Method.EXECUTE,
        new Channel.Params(getObject(), id, action, actionArg, object, returnType, options), options, callback);
  }

  protected <R> R execute(String action, Object object, Class<R> returnType, Options options, Callback<R> callback) {
    return execute(getAppId(), action, object, returnType, options, callback);
  }

  protected <R> ObjectList<R> executeToList(String action, Object object, Class<R> returnType, Options options,
                                            Callback<ObjectList<R>> callback) {
    return executeToList(getAppId(), action, object, returnType, options, callback);
  }

  protected <R> R execute(String appId, String action, Object object, Class<R> returnType, Options options,
                          Callback<R> callback) {
    return request(Channel.Method.EXECUTE, Channel.Params.forApp(appId, action, object, returnType, options), options,
        callback);
  }

  protected <R> ObjectList<R> executeToList(String appId, String action, Object object, Class<R> returnType, Options options,
                                            Callback<ObjectList<R>> callback) {
    return requestList(Channel.Method.EXECUTE, Channel.Params.forApp(appId, action, object, returnType, options),
        options, callback);
  }

  protected Boolean executeToBool(String id, String action, String actionArg, Object object, Options options,
                                  Callback<Boolean> callback) {
    Converter<Result, Boolean> converter = Converter.RESULT2BOOL;
    CallbackAdapter<Result, Boolean> cb = callback == null ? null : new CallbackAdapter<>(callback, converter);
    Result result = request(Channel.Method.EXECUTE, new Channel.Params(getObject(), id, action, actionArg, object,
        Result.class, options), options, cb);
    return converter.convert(result);
  }

  protected Boolean executeToBool(String action, Object object, Options options, Callback<Boolean> callback) {
    return executeToBool(getAppId(), action, object, options, callback);
  }

  protected Boolean executeToBool(String appId, String action, Object object, Options options, Callback<Boolean> callback) {
    Converter<Result, Boolean> converter = Converter.RESULT2BOOL;
    CallbackAdapter<Result, Boolean> cb = callback == null ? null : new CallbackAdapter<>(callback, converter);
    Result result = request(Channel.Method.EXECUTE, Channel.Params.forApp(appId, action, object, Result.class, options),
        options, cb);
    return converter.convert(result);
  }

  // ---------------------------------------------------------------------------------------------------

  private <R> R request(Channel.Method method, Channel.Params p, Options options, final Callback<R> callback) {
    if (options == null) {
      options = getDefaultOptions();
    }
    if (p.options == null) {
      p.options = options;
    }
    final Callback.Notify pre = options.resultProcessing;
    Channel channel = channel(options);
    if (callback == null) {
      try {
        R result = channel.request(method, p, null);
        if (pre != null) {
          pre.notify(result);
        }
        return result;
      } catch (Exception e) {
        handleException(e);
      }
    } else {
      channel.request(method, p, new Callback<R>() {
        public void completed(R result) {
          if (pre != null) {
            pre.notify(result);
          }
          callback.completed(result);
        }

        public void failed(Throwable cause) {
          Exception ex = ExceptionMapper.map(cause, null);
          if (context.exceptionHandler == null) {
            callback.failed(ex);
          } else {
            context.exceptionHandler.handle(ex);
          }
        }
      });
    }
    return null;
  }

  private <R> ObjectList<R> requestList(Channel.Method method, Channel.Params p, Options options,
                                        final Callback<ObjectList<R>> callback) {
    if (options == null) {
      options = getDefaultOptions();
    }
    if (p.options == null) {
      p.options = options;
    }
    final Callback.Notify processor = options.resultProcessing;
    Channel channel = channel(options);
    if (callback == null) {
      try {
        ObjectList<R> result = channel.requestList(method, p, null);
        if (processor != null) {
          processor.notify(result);
        }
        return result;
      } catch (Exception e) {
        handleException(e);
      }
    } else {
      channel.requestList(method, p, new Callback<ObjectList<R>>() {
        public void completed(ObjectList<R> result) {
          if (processor != null) {
            processor.notify(result);
          }
          callback.completed(result);
        }

        public void failed(Throwable cause) {
          Exception ex = ExceptionMapper.map(cause, null);
          if (context.exceptionHandler == null) {
            callback.failed(ex);
          } else {
            context.exceptionHandler.handle(ex);
          }
        }
      });

    }
    return null;
  }

  /**
   * Translate and pass given throwable to the exception handler if any, else just throw.
   */
  private void handleException(Throwable throwable) {
    RuntimeException ex = ExceptionMapper.map(throwable, null);
    if (context.exceptionHandler == null) {
      throw ex;
    }
    context.exceptionHandler.handle(ex);
  }

  /**
   * Select and return an API communication channel according to the provided options.
   */
  private Channel channel(Options options) {
    // select channel according to special product preferences or fall back to default
    if (options.channel == null) {
      options.channel = context.defaultChannel;
    }
    Channel ch = context.channels.get(options.channel);

    if (ch == null) {
      throw new IllegalArgumentException("Channel " + options.channel + " not available. (Maybe disabled?)");
    }
    return ch;
  }

  private String[] getObject() {
    return getMetaData().getObjectArray();
  }

  private String getAppId() {
    return getMetaData().getAppId();
  }

  private Class<T> getResourceType() {
    return getMetaData().resourceType;
  }

  /**
   * Downloads given media resource if necessary.
   */
  protected static void downloadMedia(MediaResource picture) {
    if (picture != null) {
      if (!picture.isCached()) {
        picture.download();
      }
    }
  }

  /**
   * Meta data describing the service.
   */
  public static class ServiceMetaData<T extends SecuObject> {
    public final String product;
    public final String resource;
    public final String appId;
    public final Class<T> resourceType;

    public ServiceMetaData(String product, String resource, Class<T> resourceType) {
      this.product = product;
      this.resource = resource;
      this.resourceType = resourceType;
      this.appId = null;
    }

    public ServiceMetaData(String appId) {
      this.appId = appId;
      this.product = null;
      this.resource = null;
      this.resourceType = null;
    }

    public Class<T> getResourceType() {
      return resourceType;
    }

    public String getAppId() {
      return appId;
    }

    /**
     * Returns the object string for the service.
     */
    public String getObject() {
      return product + "." + resource;
    }

    public String[] getObjectArray() {
      return new String[]{product, resource};
    }
  }

}
