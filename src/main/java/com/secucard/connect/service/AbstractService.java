package com.secucard.connect.service;

import com.secucard.connect.client.ClientContext;
import com.secucard.connect.event.EventListener;

public abstract class AbstractService {
  public abstract void setContext(ClientContext context);

  public abstract void setEventListener(EventListener eventListener);
}
