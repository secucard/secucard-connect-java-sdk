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

/**
 * Event handler callback which delegates all notifications to a provided callback rather then expose the own
 * callback interface. This is useful when a simple {@link com.secucard.connect.client.Callback} should be provided as
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
