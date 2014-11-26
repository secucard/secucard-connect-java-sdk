package com.secucard.connect.service;

import com.secucard.connect.channel.Channel;
import com.secucard.connect.client.ClientContext;
import com.secucard.connect.client.ExceptionHandler;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.event.Events;

import java.util.logging.Logger;

public abstract class AbstractService {
  protected ClientContext context;
  protected final Logger LOG = Logger.getLogger(getClass().getName());

  public void setContext(ClientContext context) {
    this.context = context;
  }

  protected Channel getChannnel() {
    return context.getChannnel(context.getConfig().getDefaultChannel());
  }

  protected Channel getStompChannel() {
    return context.getStompChannel();
  }

  protected Channel getRestChannel() {
    return context.getRestChannel();
  }

  protected void handleException(Exception exception) {
    context.getExceptionHandler().handle(exception);
  }
}
