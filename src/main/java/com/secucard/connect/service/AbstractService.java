package com.secucard.connect.service;

import com.secucard.connect.Callback;
import com.secucard.connect.ClientContext;
import com.secucard.connect.ExceptionHandler;
import com.secucard.connect.auth.AuthProvider;
import com.secucard.connect.channel.Channel;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.event.Events;
import com.secucard.connect.model.transport.Result;
import com.secucard.connect.util.CallbackAdapter;
import com.secucard.connect.util.Converter;
import com.secucard.connect.util.ThreadLocalUtil;

import java.util.logging.Logger;

public abstract class AbstractService {
  protected ClientContext context;
  protected final Logger LOG = Logger.getLogger(getClass().getName());

  public void setContext(ClientContext context) {
    this.context = context;
  }

  protected Channel getChannel() {
    return context.getChannel();
  }

  protected Channel getStompChannel() {
    return context.getStompChannel();
  }

  protected Channel getRestChannel() {
    return context.getRestChannel();
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

  /**
   * Helps to wrap the execution of code to handle exceptions and callbacks in a standartized way.
   * If a callback is used all exceptions go to failed() method otherwise (direct return) they will be forwarded to
   * the services exception handler set by
   * {@link com.secucard.connect.Client#setExceptionHandler(com.secucard.connect.ExceptionHandler)}.
   *
   * @param <T> The excpected return type.
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
              return AbstractService.ConvertingInvoker.this.convert(value);
            }
          });
      return convert(invoke(adapter));
    }
  }

  protected abstract class Result2BooleanInvoker extends AbstractService.ConvertingInvoker<Result, Boolean> {
    @Override
    protected Boolean convert(Result object) {
      return object == null ? Boolean.FALSE : Boolean.parseBoolean(object.getResult());
    }
  }
}
