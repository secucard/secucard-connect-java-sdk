package com.secucard.connect.event;

import com.secucard.connect.model.general.Event;

import java.util.HashMap;
import java.util.Map;

/**
 * Registers event handlers or listeners and dispatches events to them.
 */
public class EventDispatcher {
  // event type to listener map
  private final Map<Object, EventListener> listeners = new HashMap<>();

  private boolean allowMultipleListener = false;

  public void setAllowMultipleListener(boolean allowMultipleListener) {
    this.allowMultipleListener = allowMultipleListener;
  }

  /**
   * Register an event listener. The listeners accept() method determines if a particular event is handled or not.
   * So multiple listener could be attached to an event, but only if the {@link #allowMultipleListener} setting is set
   * to true (default false). Otherwise the event dispatching stops if the first listener accepts.
   * Replaces previous registered listeners for the given id.
   *
   * @param id       An unique id associated to the listener.
   * @param listener The listener to register. Pass null to delete the for listener.
   */
  public synchronized void registerListener(String id, AbstractEventListener listener) {
    if (listener == null) {
      listeners.remove(id);
    } else {
      listeners.put(id, listener);
    }
  }

  /**
   * Register a event listener which will be called when an event of certain type happens.
   * Replaces previous registered listeners for the type.
   * This way only a single listener can be attached to an event type.
   *
   * @param eventType The event class.
   * @param listener  The listener to register. Pass null to delete the listener.
   * @param <T>       The actual event type.
   */
  public synchronized <T> void registerListener(Class<T> eventType, EventListener<T> listener) {
    if (listener == null) {
      listeners.remove(eventType);
    } else {
      listeners.put(eventType, listener);
    }
  }

  /**
   * Dispatch an event object to registered listeners.
   *
   * @param event The event data.
   * @param async Dispatch asynchronous or not, if not the methods block until the event was processed.
   * @return True if at least one matching listener could be found, else false.
   */
  @SuppressWarnings("unchecked")
  public synchronized boolean dispatch(Object event, boolean async) {
    int count = 0;
    for (Map.Entry<Object, EventListener> entry : listeners.entrySet()) {
      boolean accept = false;
      EventListener listener = entry.getValue();
      if (listener instanceof AbstractEventListener && event instanceof Event) {
        accept = ((AbstractEventListener) listener).accept((Event) event);
      } else {
        accept = event.getClass().equals(entry.getKey());
      }
      if (accept) {
        count++;
        fireEvent(event, listener, async);
        if (!allowMultipleListener) {
          // stop  at the first match
          break;
        }
      }
    }
    return count > 0;
  }

  /**
   * Delivers a given event to a single event listener.
   * Calling the listener happens in this method callers thread or in a new thread depending on the given parameter.
   * In the first case the method returns not until the listener method is completely executed.
   * The latter cases causes this method to return immediately instead of waiting for the listener to return.
   *
   * @param event    The actual event data.
   * @param listener The listener which should receive the event.
   * @param async    True if asynchronous delivery, false else.
   * @param <T>      The actual event type.
   */
  public static <T> void fireEvent(final T event, final EventListener<T> listener, boolean async) {
    if (listener != null) {
      if (async) {
        Thread thread = new Thread() {
          @Override
          public void run() {
            listener.onEvent(event);
          }
        };
        thread.setDaemon(true);
        thread.start();
      } else {
        listener.onEvent(event);
      }
    }
  }
}
