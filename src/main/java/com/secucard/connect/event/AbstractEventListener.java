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

import com.secucard.connect.product.general.model.Event;

/**
 * A listener which gets notified when any kind of event happens and is able to tell
 * if an event of certain type would be accepted at all.
 *
 * @param <T> The actual event type.
 */
public abstract class AbstractEventListener<T> implements EventListener<Event<T>> {

  /**
   * Specifies if the given event will be processed by the listener.
   * Override to implement special behaviour.
   * The default implementation returns always true.
   *
   * @param event The event data.
   * @return True if accepted else false.
   */
  public abstract boolean accept(Event<T> event);
}
