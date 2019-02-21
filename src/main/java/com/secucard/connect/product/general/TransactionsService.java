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

package com.secucard.connect.product.general;

import com.secucard.connect.client.Callback;
import com.secucard.connect.client.ProductService;
import com.secucard.connect.event.AbstractEventListener;
import com.secucard.connect.event.DelegatingEventHandlerCallback;
import com.secucard.connect.event.Events;
import com.secucard.connect.product.general.model.Event;
import com.secucard.connect.product.general.model.Transaction;
import java.util.Map;

/**
 * Implements the general/transactions operations.
 */
public class TransactionsService extends ProductService<Transaction> {

  public static final ServiceMetaData<Transaction> META_DATA = new ServiceMetaData<>("general", "transactions", Transaction.class);

  @Override
  public ServiceMetaData<Transaction> getMetaData() {
    return META_DATA;
  }

  /**
   * Set a callback to get notified when a transaction has changed.
   */
  public void onTransactionsChanged(final Callback<Transaction> callback) {
    AbstractEventListener listener = null;

    if (callback != null) {
      listener = new DelegatingEventHandlerCallback<Event, Transaction>(callback) {
        @Override
        public boolean accept(Event event) {
          return Events.TYPE_ADDED.equals(event.getType()) && getMetaData().getObject().equals(event.getTarget());
        }

        @Override
        protected Transaction process(Event event) {
          String id = ((Map) event.getData()).get("id").toString();
          return get(id, this);
        }
      };
    }

    context.eventDispatcher.registerListener(getMetaData().getObject() + Events.TYPE_ADDED, listener);
  }
}
