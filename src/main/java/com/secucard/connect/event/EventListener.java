package com.secucard.connect.event;

/**
 * A listener which gets notified when any kind of event happens.
 *
 * @param <T> The actual event type.
 */
public interface EventListener<T> {

  /**
   * Gets called when an event happens.
   *
   * @param event The event data.
   */
  public abstract void onEvent(T event);
}
