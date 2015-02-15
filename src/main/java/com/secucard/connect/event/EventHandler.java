package com.secucard.connect.event;

import com.secucard.connect.Callback;

/**
 * Handles events of a particular type.
 *
 * @param <E> The actual event type.
 * @param <R> The actual event processing result type.
 */
public abstract class EventHandler<R, E> {
  private boolean disabled = false;

  /**
   * Returns if this event is disabled or not.
   */
  public final boolean isDisabled() {
    return disabled;
  }

  /**
   * Sets if this event handler is disabled (no event processing) or not.
   */
  public final void setDisabled(boolean disabled) {
    this.disabled = disabled;
  }

  /**
   * Indicates if the given event will be processed by this handler.
   */
  public abstract boolean accept(E event);

  /**
   * Implements the actual event processing.
   *
   * @param callback The callback for async result delivery.
   * @return If no callback is used the processing result, may be of type Void if no result is involved. Null if a
   * callback is used.
   */
  public abstract R handle(E event, Callback<R> callback);
}
