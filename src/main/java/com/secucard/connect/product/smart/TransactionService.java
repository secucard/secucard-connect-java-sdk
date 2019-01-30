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
import com.secucard.connect.product.loyalty.model.LoyaltyBonus;
import com.secucard.connect.product.smart.model.Transaction;

/**
 * Implements the smart/transaction operations.
 */
public class TransactionService extends ProductService<Transaction> {
  public static final ServiceMetaData<Transaction> META_DATA = new ServiceMetaData<>("smart", "transactions",
      Transaction.class);

  private static final String GENERAL_NOTIFICATIONS_OBJECT = "general.notifications";

  public static final String TYPE_DEMO = "demo";
  public static final String TYPE_CASH = "cash";
  public static final String TYPE_AUTO = "auto";
  public static final String TYPE_ZVT = "cashless";
  public static final String TYPE_LOYALTY = "loyalty";

  @Override
  public ServiceMetaData<Transaction> getMetaData() {
    return META_DATA;
  }

  /**
   * Starting/Executing a transaction.
   * An event of type {@link com.secucard.connect.product.smart.model.CashierDisplay} may happen during the execution.
   *
   * @param transactionId The transaction id.
   * @param type          The transaction type like "auto" or "cash".
   * @return The result data.
   */
  public Transaction start(String transactionId, String type, Callback<Transaction> callback)
  {
    if (transactionId == null || transactionId.equals("")) {
      throw new IllegalArgumentException("Parameter [transactionId] can not be empty!");
    }

    if (type == null || type.equals("")) {
      throw new IllegalArgumentException("Parameter [type] can not be empty!");
    }

    return super.execute(transactionId, "start", type, null, Transaction.class, new Options(Options.CHANNEL_STOMP), callback);
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
          return Events.TYPE_DISPLAY.equals(event.getType()) && GENERAL_NOTIFICATIONS_OBJECT.equals(event.getTarget());
        }

        @Override
        protected Notification process(Event<Notification> event) {
          return event.getData();
        }
      };
    }

    context.eventDispatcher.registerListener(Events.TYPE_DISPLAY + GENERAL_NOTIFICATIONS_OBJECT, listener);
  }

  /**
   * Cancel the existing transaction with the given id.
   * @param id Id of the transaction
   * @return True if ok false else.
   */
  public Boolean cancel(String id)
  {
    if (id == null || id.equals("")) {
      throw new IllegalArgumentException("Parameter [id] can not be empty!");
    }

    return executeToBool(id, "cancel", null, "smart.transactions", null, null);
  }

  /**
   * Cancel the existing transaction with the given id.
   * @param id Id of the transaction
   * @return The result data.
   */
  public Transaction cancel(String id, Callback<Transaction> callback)
  {
    if (id == null || id.equals("")) {
      throw new IllegalArgumentException("Parameter [id] can not be empty!");
    }

    return execute(id, "cancel", null, "smart.transactions", Transaction.class, null, callback);
  }

  /**
   * Starts extended Diagnose
   * @return Transaction
   */
  public Transaction diagnosis(Callback<Transaction> callback)
  {
    return execute(null, "Diagnosis", null, "smart.transactions", Transaction.class, new Options(Options.CHANNEL_STOMP), callback);
  }

  /**
   * Starts End of Day Report (Kassenschnitt)
   * @return Transaction
   */
  public Transaction endOfDay(Callback<Transaction> callback)
  {
    return execute(null, "EndofDay", null, "smart.transactions", Transaction.class, new Options(Options.CHANNEL_STOMP), callback);
  }

  /**
   * Cancel payment transaction different from Loyalty
   * @param receiptNumber Receipt number to cancel
   * @return Transaction
   */
  public Transaction cancelPayment(String receiptNumber, Callback<Transaction> callback)
  {
    if (receiptNumber == null || receiptNumber.equals("")) {
      throw new IllegalArgumentException("Parameter [receiptNumber] can not be empty!");
    }

    return execute(receiptNumber, "cancelTrx", null, "smart.transactions", Transaction.class, new Options(Options.CHANNEL_STOMP), callback);
  }

  /**
   * Request loyalty bonus products and add them to the basket
   * @param id Id of the smart transaction
   * @return LoyaltyBonus
   */
  public LoyaltyBonus appendLoyaltyBonusProducts(String id, Callback<LoyaltyBonus> callback)
  {
    if (id == null || id.equals("")) {
      throw new IllegalArgumentException("Parameter [id] can not be empty!");
    }

    return execute(id, "preTransaction", null, "smart.transactions", LoyaltyBonus.class, null, callback);
  }
}
