package com.secucard.connect.event;

import com.secucard.connect.Callback;

/**
 * Handles events of a particular type
 * and returns the results by callback methods.
 *
 * @param <E> The actual event type.
 * @param <R> The actual event processing result type.
 */
public abstract class AbstractEventHandler<R, E> implements Callback<R> {
  private boolean disabled = false;

  /**
   * Returns if this event is disabled or not.
   */
  public final boolean isDisabled() {
    return disabled;
  }

  protected boolean isAsync(){
    return true;
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
   */
  public abstract void handle(E event);
}
