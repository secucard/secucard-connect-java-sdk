package com.secucard.connect.service.smart;

import com.secucard.connect.Callback;
import com.secucard.connect.ClientContext;
import com.secucard.connect.event.EventHandler;
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
    return create(transaction, callback, null);
  }

  /**
   * Updating a transaction.
   *
   * @param transaction The transaction data to update.
   * @return The updated transaction. Use this instance for further processing rather the the provided..
   */
  public Transaction updateTransaction(final Transaction transaction, Callback<Transaction> callback) {
    return update(transaction, callback, null);
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
    return execute(Transaction.class, transactionId, "start", type, null, Transaction.class,
        callback, ClientContext.STOMP);
  }

  public void onCashierDisplayChanged(Callback<Notification> callback) {
    addOrRemoveEventHandler(Events.TYPE_DISPLAY + Notification.OBJECT, new NotificationEventEventHandler(callback), callback);
  }

  private class NotificationEventEventHandler extends EventHandler<Notification, Event> {
    public NotificationEventEventHandler(Callback<Notification> callback) {
      super(callback);
    }

    @Override
    public boolean accept(Event event) {
      return Events.TYPE_DISPLAY.equals(event.getType()) && Notification.OBJECT.equals(event.getTarget());
    }

    @Override
    public void handle(Event event) {
      completed((Notification) event.getData());
    }
  }
}
