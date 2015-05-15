package com.secucard.connect.service.smart;

import com.secucard.connect.Callback;
import com.secucard.connect.channel.Channel;
import com.secucard.connect.event.AbstractEventListener;
import com.secucard.connect.event.DelegatingEventHandlerCallback;
import com.secucard.connect.event.Events;
import com.secucard.connect.model.general.Event;
import com.secucard.connect.model.general.Notification;
import com.secucard.connect.model.smart.Transaction;
import com.secucard.connect.service.AbstractService;

/**
 * The Smart Product operations.
 */
public class TransactionService extends AbstractService {

  /**
   * Creating a transaction.
   *
   * @param transaction The transaction data to save.
   * @return The new transaction. Use this instance for further processing rather the the provided..
   */
  public Transaction createTransaction(final Transaction transaction, Callback<Transaction> callback) {
    return new ServiceTemplate(Channel.STOMP).create(transaction, callback);
  }

  /**
   * Updating a transaction.
   *
   * @param transaction The transaction data to update.
   * @return The updated transaction. Use this instance for further processing rather the the provided..
   */
  public Transaction updateTransaction(final Transaction transaction, Callback<Transaction> callback) {
    return new ServiceTemplate(Channel.STOMP).update(transaction, callback);
  }

  /**
   * Starting/Executing a transaction.
   * An event of type {@link com.secucard.connect.model.smart.CashierDisplay} may happen during the execution.
   *
   * @param transactionId The transaction id.
   * @param type          The transaction type like "auto" or "cash".
   * @return The result data.
   */
  public Transaction startTransaction(final String transactionId, final String type,
                                      Callback<Transaction> callback) {
    return new ServiceTemplate(Channel.STOMP).execute(Transaction.class, transactionId, "start", type, null,
        Transaction.class, callback);
  }

  public void onCashierDisplayChanged(Callback<Notification> callback) {
    AbstractEventListener listener = null;

    if (callback != null) {
      listener = new DelegatingEventHandlerCallback<Event<Notification>, Notification>(callback) {
        @Override
        public boolean accept(Event event) {
          return Events.TYPE_DISPLAY.equals(event.getType()) && Notification.OBJECT.equals(event.getTarget());
        }

        @Override
        protected Notification process(Event<Notification> event) {
          return event.getData();
        }
      };
    }

    getEventDispatcher().registerListener(Events.TYPE_DISPLAY + Notification.OBJECT, listener);
  }
}
