package com.secucard.connect.client;

import com.secucard.connect.event.EventListener;

public abstract class AbstractService {
  public abstract void setContext(ClientContext context);

  public abstract void setEventListener(EventListener eventListener);
}
