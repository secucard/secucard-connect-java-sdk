package com.secucard.connect.event;

import com.secucard.connect.client.Callback;
import com.secucard.connect.product.general.model.Event;

/**
 * Event listener which receives an event and processes it by executing arbitrary operations.
 * Afterwards the operation result is delivered by its callback methods.
 *
 * @param <E> The actual event object type.
 * @param <R> The result type.
 */
public abstract class EventHandlerCallback<E, R> extends AbstractEventListener<E> implements Callback<R> {

  @Override
  public void onEvent(Event<E> event) {
    try {
      completed(process(event));
    } catch (Throwable t) {
      failed(t);
    }
  }

  /**
   * Implements the actual event processing.
   *
   * @param event The event data.
   * @return The processing result.
   */
  protected abstract R process(Event<E> event);

  /**
   * Gets called when the processing result is ready.
   *
   * @param result The result data.
   */
  public abstract void completed(R result);

  /**
   * Gets called when an error happened during processing.
   *
   * @param t The cause.
   */
  public abstract void failed(Throwable t);
}
