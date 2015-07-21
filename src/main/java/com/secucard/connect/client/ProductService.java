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
import com.secucard.connect.product.common.model.ObjectList;
import com.secucard.connect.product.common.model.QueryParams;
import com.secucard.connect.product.common.model.Result;
import com.secucard.connect.product.common.model.SecuObject;
import com.secucard.connect.util.CallbackAdapter;
import com.secucard.connect.util.Converter;
import com.secucard.connect.util.ExceptionMapper;

import java.util.List;

public abstract class ProductService<T extends SecuObject> {
  private ServiceMetaData<T> metaData;
  protected ClientContext context;

  public void setContext(ClientContext context) {
    this.context = context;
  }

  /**
   * Creates meta data associated with this product service.
   * Don't use for retrieval, use {@link #getMetaData()} instead.
   */
  protected abstract ServiceMetaData<T> createMetaData();


  protected Options getDefaultOptions() {
    return Options.getDefault();
  }

  public ServiceMetaData<T> getMetaData() {
    if (metaData == null) {
      metaData = createMetaData();
    }
    return metaData;
  }

  // get, get list -----------------------------------------------------------------------------------------------------

  public T get(String id) {
    return get(id, null);
  }

  public T get(String id, Callback<T> callback) {
    return get(id, null, callback);
  }

  protected T get(String id, Options options, Callback<T> callback) {
    return request(Channel.Method.GET,
        new Channel.Params(getObject(), id, getResourceType(), options), options, callback);
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
   * @throws com.secucard.connect.client.SecucardConnectException if an error happens.
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
   * If the resource can't be created or another error happens SecucardConnectException will be thrown.
   * Inspect the code and userMessage field to get info about the error cause.
   *
   * @param object   The resource to create.
   * @param callback Callback receiving the result asynchronous.
   * @throws com.secucard.connect.client.SecucardConnectException if an error happens.
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
   * If the resource can't be updated or another error happens SecucardConnectException will be thrown.
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

  protected <R> R execute(String appId, String action, Object object, Class<R> returnType, Options options,
                          Callback<R> callback) {
    return request(Channel.Method.EXECUTE, Channel.Params.forApp(appId, action, object, returnType, options), options,
        callback);
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
          SecucardConnectException ex = ExceptionMapper.map(cause);
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
      channel.request(method, p, new Callback<ObjectList<R>>() {
        public void completed(ObjectList<R> result) {
          if (processor != null) {
            processor.notify(result);
          }
          callback.completed(result);
        }

        public void failed(Throwable cause) {
          SecucardConnectException ex = ExceptionMapper.map(cause);
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
    SecucardConnectException ex = ExceptionMapper.map(throwable);
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
      throw new IllegalArgumentException("Can't use " + ProductService.this.getClass().getSimpleName() + " with "
          + options.channel);
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
