package com.secucard.connect.service;

import com.secucard.connect.Callback;
import com.secucard.connect.ClientContext;
import com.secucard.connect.ExceptionHandler;
import com.secucard.connect.auth.AuthProvider;
import com.secucard.connect.channel.Channel;
import com.secucard.connect.event.AbstractEventHandler;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.event.Events;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.QueryParams;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.general.Event;
import com.secucard.connect.model.transport.Result;
import com.secucard.connect.util.CallbackAdapter;
import com.secucard.connect.util.Converter;
import com.secucard.connect.util.ThreadLocalUtil;

import java.util.List;
import java.util.logging.Logger;

public abstract class AbstractService {
  protected ClientContext context;
  protected final Logger LOG = Logger.getLogger(getClass().getName());
  protected EventListener serviceEventListener;

  public static enum Constant {
    EVENT_SKIPPED
  }

  public void setContext(ClientContext context) {
    this.context = context;
  }

  /**
   * See {@link ClientContext#getChannel(String)}
   */
  protected Channel getChannel() {
    return context.getChannel(null);
  }

  /**
   * See {@link ClientContext#getChannel(String)}
   */
  protected Channel getStompChannel() {
    return context.getChannel(ClientContext.STOMP);
  }

  /**
   * See {@link ClientContext#getChannel(String)}
   */
  protected Channel getRestChannel() {
    return context.getChannel(ClientContext.REST);
  }

  protected AuthProvider getAuthProvider() {
    return context.getAuthProvider();
  }

  protected void handleException(Throwable exception, Callback callback) {
    if (callback != null) {
      callback.failed(exception);
    }

    ExceptionHandler exceptionHandler = context.getExceptionHandler();
    if (exceptionHandler != null) {
      exceptionHandler.handle(exception);
    }
  }

  protected <FROM, TO> CallbackAdapter<FROM, TO> getCallbackAdapter(Callback<TO> callback, Converter<FROM, TO> converter) {
    return callback == null ? null : new CallbackAdapter<>(callback, converter);
  }

  /**
   * Assign client context to current thread of execution so it can be accessed from objects not having a
   * reference to the client.
   */
  protected void setContext() {
    ThreadLocalUtil.set(ClientContext.class.getName(), context);
  }

  /**
   * Remove all data assigned to threads.
   */
  protected void clear() {
    ThreadLocalUtil.remove();
  }

  /**
   * Service initialization method to override for special initialization. The default does nothing.
   * Should get called after construction of a service instance and when all dependencies are set.
   */
  public void init() {

  }

  public void setEventListener(final EventListener eventListener) {
    context.getEventDispatcher().setEventListener(Events.ANY, eventListener);

    // we use the same listener also for auth event purposes
    getAuthProvider().registerEventListener(eventListener);
  }

  public <T> void setEventListener(Class<T> type, EventListener<T> listener) {
    context.getEventDispatcher().setEventListener(type, listener);
  }

  public void removeEventListener() {
    context.getEventDispatcher().removeEventListener();
    getAuthProvider().registerEventListener(null);
  }

  public EventListener getServiceEventListener() {
    return serviceEventListener;
  }

  protected void addEventHandler(String id, AbstractEventHandler<?, Event> handler) {
    context.getEventDispatcher().addEventHandler(id, handler);
  }

  protected void addOrRemoveEventHandler(String id, AbstractEventHandler<?, Event> handler) {
    if (handler == null) {
      removeEventHandler(id);
    } else {
      addEventHandler(id, handler);
    }
  }

  protected void removeEventHandler(String id) {
    context.getEventDispatcher().removeEventHandler(id);
  }

  protected void disableEventHandler(String id, boolean disabled) {
    context.getEventDispatcher().disableEventHandler(id, disabled);
  }

  /**
   * Return an object.<br/>
   *
   * @param type     Actual object element type.
   * @param id       Object id.
   * @param callback Callback for async processing.
   * @param channel  The channel to use, like {@link com.secucard.connect.ClientContext#REST}. Pass null to use default channel.
   * @return The object.
   */
  public <T> T get(final Class<T> type, final String id, final Callback<T> callback,
                   final String channel) {
    return new Invoker<T>() {
      @Override
      protected T handle(Callback<T> callback) throws Exception {
        return context.getChannel(channel).getObject(type, id, callback);
      }
    }.invoke(callback);
  }

  /**
   * Return a list of objects.<br/>
   * Note: Add list post processing by implementing {@link #postProcessObjects(java.util.List)}. This method is called
   * after the list was retrieved.
   *
   * @param type        Actual list element type.
   * @param queryParams Query params
   * @param callback    Callback for async processing.
   * @param channel     The channel to use, like {@link com.secucard.connect.ClientContext#REST}.
   *                    Pass null to use default channel.
   * @return The objects.
   */
  public <T> List<T> getList(final Class<T> type, final QueryParams queryParams, final Callback<List<T>> callback,
                             final String channel) {
    return new ConvertingInvoker<ObjectList<T>, List<T>>() {
      @Override
      protected ObjectList<T> handle(Callback<ObjectList<T>> callback) {
        return context.getChannel(channel).findObjects(type, queryParams, callback);
      }

      @Override
      protected List<T> convert(ObjectList<T> objectList) {
        if (objectList == null || objectList.getCount() == 0) {
          return null;
        }
        List<T> objects = objectList.getList();
        setContext();
        postProcessObjects(objects);
        return objects;
      }
    }.invokeAndConvert(callback);
  }

  /**
   * Return a list of objects wrapped in ObjectList.<br/>
   * Note: Add list post processing by implementing {@link #postProcessObjects(java.util.List)}. This method is called
   * after the list was retrieved.
   *
   * @param type        Actual list element type.
   * @param queryParams Query params
   * @param callback    Callback for async processing.
   * @param channel     The channel to use, like {@link com.secucard.connect.ClientContext#REST}.
   *                    Pass null to use default channel.
   * @return The objects.
   */
  public <T> ObjectList<T> getObjectList(final Class<T> type, final QueryParams queryParams,
                                         final Callback<ObjectList<T>> callback,
                                         final String channel) {
    return new Invoker<ObjectList<T>>() {
      @Override
      protected ObjectList<T> handle(Callback<ObjectList<T>> callback) {
        ObjectList<T> objectList = context.getChannel(channel).findObjects(type, queryParams, callback);
        if (objectList == null || objectList.getCount() == 0) {
          return null;
        }
        setContext();
        postProcessObjects(objectList.getList());
        return objectList;
      }
    }.invoke(callback);
  }

  protected void postProcessObjects(List<?> objects) {

  }

  protected <T extends SecuObject> T update(final T object, Callback<T> callback, final String channel) {
    return new Invoker<T>() {
      @Override
      protected T handle(Callback<T> callback) throws Exception {
        return context.getChannel(channel).updateObject(object, callback);
      }
    }.invoke(callback);
  }

  public <T> T execute(final Class type, final String objectId, final String action, final String actionArg,
                       final Object arg, final Class<T> returnType, Callback<T> callback, final String channel) {
    return new Invoker<T>() {
      @Override
      protected T handle(Callback<T> callback) throws Exception {
        return context.getChannel(channel).execute(type, objectId, action, actionArg, arg, returnType, callback);
      }
    }.invoke(callback);
  }

  public <T> T execute(final String appId, final String action, final Object arg, final Class<T> returnType,
                       Callback<T> callback, final String channel) {
    return new Invoker<T>() {
      @Override
      protected T handle(Callback<T> callback) throws Exception {
        return context.getChannel(channel).execute(appId, action, arg, returnType, callback);
      }
    }.invoke(callback);
  }

  protected <T> T create(final T object, Callback<T> callback, final String channel) {
    return new Invoker<T>() {
      @Override
      protected T handle(Callback<T> callback) throws Exception {
        return context.getChannel(channel).createObject(object, callback);
      }
    }.invoke(callback);
  }

  public void delete(final Class type, final String id, Callback<Void> callback, final String channel) {
    new Invoker<Void>() {
      @Override
      protected Void handle(Callback<Void> callback) throws Exception {
        context.getChannel(channel).deleteObject(type, id, callback);
        return null;
      }
    }.invoke(callback);
  }

  public void delete(final Class type, final String objectId, final String action, final String actionArg,
                     Callback<Void> callback, final String channel) {
    new Invoker<Void>() {
      @Override
      protected Void handle(Callback<Void> callback) throws Exception {
        context.getChannel(channel).deleteObject(type, objectId, action, actionArg, callback);
        return null;
      }
    }.invoke(callback);
  }

  /**
   * Helps to wrap the execution of code to handle exceptions and callbacks in a standardized way.
   * If a callback is used all exceptions go to failed() method otherwise (direct return) they will be forwarded to
   * the services exception handler set by
   * {@link com.secucard.connect.Client#setExceptionHandler(com.secucard.connect.ExceptionHandler)}.
   *
   * @param <T> The expected return type.
   */
  protected abstract class Invoker<T> {

    /**
     * Implements the actual code to execute.
     */
    protected abstract T handle(Callback<T> callback) throws Exception;

    public T invoke(Callback<T> callback) {
      try {
        return handle(callback);
      } catch (Throwable e) {
        handleException(e, callback);
      }
      return null;
    }
  }

  /**
   * Like {@link com.secucard.connect.service.AbstractService.Invoker}.
   * Additionally supports converting between result types for direct return as well for callbacks.
   *
   * @param <FROM> The intermediate result type to convert from.
   * @param <TO>   The target result type to convert to.
   */
  protected abstract class ConvertingInvoker<FROM, TO> extends Invoker<FROM> {

    /**
     * Implements the actual conversion.
     */
    protected abstract TO convert(FROM object);

    public TO invokeAndConvert(Callback<TO> callback) {
      CallbackAdapter<FROM, TO> adapter = callback == null ? null : new CallbackAdapter<>(callback,
          new Converter<FROM, TO>() {
            @Override
            public TO convert(FROM value) {
              return ConvertingInvoker.this.convert(value);
            }
          });
      return convert(invoke(adapter));
    }
  }

  protected abstract class Result2BooleanInvoker extends ConvertingInvoker<Result, Boolean> {
    @Override
    protected Boolean convert(Result object) {
      return object == null ? Boolean.FALSE : Boolean.parseBoolean(object.getResult());
    }
  }

  protected class ServiceEventListener implements EventListener {
    public ServiceEventListener() {
    }

    @Override
    public void onEvent(Object event) {

    }
  }
}
