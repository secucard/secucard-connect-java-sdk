package com.secucard.connect.event;

import com.secucard.connect.model.general.Event;

public interface EventListener extends java.util.EventListener{
  public void onEvent(Event event);
}
