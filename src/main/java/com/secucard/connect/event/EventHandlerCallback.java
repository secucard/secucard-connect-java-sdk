/*
 * Copyright (c) 2015. hp.weber GmbH & Co secucard KG (www.secucard.com)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
