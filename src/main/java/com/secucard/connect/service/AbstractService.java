package com.secucard.connect.service;

import com.secucard.connect.Callback;
import com.secucard.connect.ClientContext;
import com.secucard.connect.ExceptionHandler;
import com.secucard.connect.channel.Channel;
import com.secucard.connect.model.transport.Result;
import com.secucard.connect.util.CallbackAdapter;
import com.secucard.connect.util.Converter;

import java.util.logging.Logger;

public abstract class AbstractService {
  protected ClientContext context;
  protected final Logger LOG = Logger.getLogger(getClass().getName());

  public void setContext(ClientContext context) {
    this.context = context;
  }

  protected Channel getChannel() {
    return context.getChannnel(context.getConfig().getDefaultChannel());
  }

  protected Channel getStompChannel() {
    return context.getStompChannel();
  }

  protected Channel getRestChannel() {
    return context.getRestChannel();
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
