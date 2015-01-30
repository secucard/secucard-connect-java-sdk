package com.secucard.connect.event;

import com.secucard.connect.model.general.Event;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Event handling.
 */
public class EventDispatcher {

  protected Map<Class, EventListener> eventListener = new ConcurrentHashMap<>();
  protected Map<String, EventListener> eventListener2 = new ConcurrentHashMap<>();

  public void setEventListener(final EventListener listener) {
    eventListener2.put("*", listener);
  }

  public <T> void setEventListener(Class<T> type, EventListener<T> listener) {
    eventListener.put(type, listener);
  }

  public <T> void setEventListener(String type, EventListener<T> listener) {
    eventListener2.put(type, listener);
  }

  public void removeEventListener() {
    eventListener.clear();
    eventListener2.clear();
  }

  /**
   * Like {@link #fireEvent(Object, boolean)} but NOT async.
   */
  public void fireEvent(final Object event) {
    fireEvent(event, false);
  }

  /**
   * Delivers a given event to multiple event listeners registered to this instance. A listener is called when mapped to
   * the type of event or event id or to event id {@link Events#ANY}.
   * Calling the listener happens in this method callers thread or in a new thread depending on the given parameter.
   * In the first case the method returns not until the listener method is completely executed.
   * The latter cases causes this method to return immediately instead of waiting for the listener to proceed.
   * <p/>
   * * @param async Async execution or not.
   */
  public void fireEvent(Object event, boolean async) {
    for (Map.Entry<String, EventListener> entry : eventListener2.entrySet()) {
      String key = entry.getKey();
      if (key.equals(Events.ANY) || event instanceof String && key.equalsIgnoreCase((String) event)) {
        fireEvent(event, entry.getValue(), async);
      }
    }

    for (Map.Entry<Class, EventListener> entry : eventListener.entrySet()) {
      Class<?> listenerType = entry.getKey();
      Class<?> eventType = event.getClass();
      if (event instanceof Event) {
        // unwrap event type and data
        event = ((Event) event).getData();
        eventType = event.getClass();
      }
      if (listenerType.equals(eventType)) {
        fireEvent(event, entry.getValue(), async);
      }
    }
  }

  /**
   * Delivers a given event to a single event listener.
   * Calling the listener happens in this method callers thread or in a new thread depending on the given parameter.
   * In the first case the method returns not until the listener method is completely executed.
   * The latter cases causes this method to return immediately instead of waiting for the listener to proceed.
   *
   * @param async Async execution or not.
   */
  public static <T> void fireEvent(final T event, final EventListener<T> listener, boolean async) {
    if (listener != null) {
      if (async) {
        new Thread() {
          @Override
          public void run() {
            listener.onEvent(event);
          }
        }.start();
      } else {
        listener.onEvent(event);
      }
    }
  }
}
