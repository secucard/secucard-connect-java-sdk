package com.secucard.connect.event;

import com.secucard.connect.model.general.Event;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Event handling.
 */
public class EventDispatcher {

  protected Map<Class, EventListener> eventListener = new ConcurrentHashMap<>();

  public void setEventListener(final EventListener listener) {
    eventListener.put(Events.Any.class, listener);
  }

  public <T> void setEventListener(Class<T> type, EventListener<T> listener) {
    eventListener.put(type, listener);
  }

  public void removeEventListener() {
    eventListener.clear();
  }

  /**
   * Like {@link #fireEvent(Object, boolean)} but NOT async.
   */
  public void fireEvent(final Object event) {
    fireEvent(event, false);
  }

  /**
   * Delivers a given event to multiple event listeners registered to this instance. A listener is called when mapped to
   * the type of event or to {@link com.secucard.connect.event.Events.Any}.
   * Calling the listener happens in this method callers thread or in a new thread depending on the given parameter.
   * In the first case the method returns not until the listener method is completely executed.
   * The latter cases causes this method to return immediately instead of waiting for the listener to proceed.
   * <p/>
   * * @param async Async execution or not.
   */
  public void fireEvent(Object event, boolean async) {
    for (Map.Entry<Class, EventListener> entry : eventListener.entrySet()) {
      Class<?> listenerType = entry.getKey();
      Class<?> eventType = event.getClass();
      if (event instanceof Event) {
        // unwrap event type and data
        event = ((Event) event).getData();
        eventType = event.getClass();
      }
      if (listenerType.equals(Events.Any.class) || listenerType.equals(eventType)) {
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
