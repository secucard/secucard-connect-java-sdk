package com.secucard.connect.event;

/**
 * A listener which gets notified when any kind of event happens and is able to tell
 * if an event of certain type would be accepted at all.
 *
 * @param <T> The actual event type.
 */
public abstract class AbstractEventListener<T> implements EventListener<T> {

  /**
   * Specifies if the given event will be processed by the listener.
   * Override to implement special behaviour.
   * The default implementation returns always true.
   *
   * @param event The event data.
   * @return True if accepted else false.
   */
  public abstract boolean accept(T event);
}
