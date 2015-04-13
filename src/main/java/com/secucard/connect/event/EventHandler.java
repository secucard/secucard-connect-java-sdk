package com.secucard.connect.event;

import com.secucard.connect.Callback;

/**
 * Implementation which forwards to a provided Callback instance rather to implement the methods itself.
 */
public abstract class EventHandler<R, E> extends AbstractEventHandler<R, E> {
  private final Callback<R> callback;

  public EventHandler(Callback<R> callback) {
    this.callback = callback;
  }

  @Override
  public void completed(R result) {
    callback.completed(result);
  }

  @Override
  public void failed(Throwable cause) {
    callback.failed(cause);
  }
}
