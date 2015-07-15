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
 * The Smart Product operations.
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
  public Transaction startTransaction(final String transactionId, final String type,
                                      Callback<Transaction> callback) {
    return super.execute(transactionId, "start", type, null, Transaction.class, new Options(Options.CHANNEL_STOMP), callback);
  }

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
   * Cancel an existing transaction.
   *
   * @param id       The debit object id.
   * @param callback Callback for async processing.
   * @return True if ok false else.
   */
  public Boolean cancel(String id, Callback<Boolean> callback) {
    return executeToBool(id, "cancel", null, null, callback);
  }
}
