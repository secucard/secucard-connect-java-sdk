package com.secucard.connect;

import com.secucard.connect.model.general.Event;

public interface EventListener extends java.util.EventListener{
  public void onEvent(Event event);
}
