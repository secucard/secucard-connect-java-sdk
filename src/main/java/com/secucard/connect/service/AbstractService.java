package com.secucard.connect.service;

import com.secucard.connect.Callback;
import com.secucard.connect.channel.Channel;
import com.secucard.connect.ClientContext;
import com.secucard.connect.ExceptionHandler;
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

  protected void handleException(Exception exception, Callback callback) {
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
}
