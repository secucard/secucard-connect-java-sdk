package com.secucard.connect.util;

import com.secucard.connect.event.EventListener;

public class EventUtil {

  public static void fireAsyncEvent(final Object event, final EventListener listener) {
    new Thread() {
      @Override
      public void run() {
        fireEvent(event, listener);
      }
    }.start();
  }

  public static void fireEvent(Object event, EventListener listener) {
    listener.onEvent(event);
  }
}
