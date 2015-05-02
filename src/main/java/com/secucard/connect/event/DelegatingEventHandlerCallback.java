package com.secucard.connect.event;

import com.secucard.connect.Callback;

/**
 * Event handler callback which delegates all notifications to a provided callback rather then expose the own
 * callback interface. This is useful when a simple {@link com.secucard.connect.Callback} should be provided as
 * event handler instead of this more complex class.
 *
 * @param <E> The actual event object type.
 * @param <R> The actual result type.
 */
public abstract class DelegatingEventHandlerCallback<E, R> extends EventHandlerCallback<E, R> {
  private Callback<R> callback;

  protected DelegatingEventHandlerCallback(Callback<R> callback) {
    this.callback = callback;
  }

  @Override
  public final void completed(R result) {
    callback.completed(result);
  }

  @Override
  public final void failed(Throwable t) {
    callback.failed(t);
  }
}
