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

package com.secucard.connect.product.smart;

import com.secucard.connect.client.Callback;
import com.secucard.connect.client.ProductService;
import com.secucard.connect.event.AbstractEventListener;
import com.secucard.connect.event.DelegatingEventHandlerCallback;
import com.secucard.connect.event.Events;
import com.secucard.connect.net.Options;
import com.secucard.connect.product.general.model.Event;
import com.secucard.connect.product.general.model.Notification;
import com.secucard.connect.product.smart.model.Transaction;

/**
 * Implements the smart/transaction operations.
 */
public class TransactionService extends ProductService<Transaction> {

  @Override
  protected ServiceMetaData<Transaction> createMetaData() {
    return new ServiceMetaData<>("smart", "transactions", Transaction.class);
  }

  /**
   * Starting/Executing a transaction.
   * An event of type {@link com.secucard.connect.product.smart.model.CashierDisplay} may happen during the execution.
   *
   * @param transactionId The transaction id.
   * @param type          The transaction type like "auto" or "cash".
   * @return The result data.
   */
  public Transaction start(String transactionId, String type, Callback<Transaction> callback) {
    return super.execute(transactionId, "start", type, null, Transaction.class, new Options(Options.CHANNEL_STOMP),
        callback);
  }

  /**
   * Set a callback to get notified when a cashier notification arrives.
   */
  public void onCashierDisplayChanged(Callback<Notification> callback) {
    AbstractEventListener listener = null;

    if (callback != null) {
      listener = new DelegatingEventHandlerCallback<Notification, Notification>(callback) {
        @Override
        public boolean accept(Event event) {
          return Events.TYPE_DISPLAY.equals(event.getType()) && "general.notifications".equals(event.getTarget());
        }

        @Override
        protected Notification process(Event<Notification> event) {
          return event.getData();
        }
      };
    }

    context.eventDispatcher.registerListener(Events.TYPE_DISPLAY + "general.notifications", listener);
  }

  /**
   * Cancel the existing transaction with the given id.
   *
   * @return True if ok false else.
   */
  public Boolean cancel(String id, Callback<Boolean> callback) {
    return executeToBool(id, "cancel", null, null, callback);
  }
}
