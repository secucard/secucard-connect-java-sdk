package com.secucard.connect.event;

/**
 * A listener which is attached to an event source and gets notified when any kind of event happens.
 *
 * @param <E> The actual event type.
 */
public interface EventListener<E> extends java.util.EventListener {

  /**
   * Gets called when an event happens.
   *
   * @param event The event data.
   */
  public void onEvent(E event);
}
