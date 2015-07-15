package com.secucard.connect.product.payment;

import com.secucard.connect.client.Callback;
import com.secucard.connect.client.ProductService;
import com.secucard.connect.client.SecucardConnectException;
import com.secucard.connect.event.AbstractEventListener;
import com.secucard.connect.event.DelegatingEventHandlerCallback;
import com.secucard.connect.event.Events;
import com.secucard.connect.product.general.model.Event;
import com.secucard.connect.product.payment.model.SecupayDebit;

import java.util.List;

/**
 * Provides Payment/Secupaydebit product operations.
 */
public class SecupayDebitsService extends ProductService<SecupayDebit> {

  @Override
  protected ServiceMetaData<SecupayDebit> createMetaData() {
    return new ServiceMetaData<>("payment", "secupaydebit", SecupayDebit.class);
  }

  /**
   * Cancel an existing transaction.
   *
   * @param id       The debit object id.
   * @param callback Callback for async processing.
   * @return
   */
  public Boolean cancelTransaction(final String id, Callback<Boolean> callback) {
    return executeToBool(id, "cancel", null, null, callback);
  }

  public void onSecupayDebitChanged(Callback<SecupayDebit> callback) {
    AbstractEventListener listener = null;

    if (callback != null) {
      listener = new DelegatingEventHandlerCallback<List<SecupayDebit>, SecupayDebit>(callback) {

        @Override
        public boolean accept(Event event) {
          return Events.TYPE_CHANGED.equals(event.getType()) && getMetaData().getObject().equals(event.getTarget());
        }

        @Override
        protected SecupayDebit process(Event<List<SecupayDebit>> event) {
          List<SecupayDebit> list = event.getData();
          if (list == null || list.size() == 0) {
            throw new SecucardConnectException("Invalid event data, missing debit ids.");
          } else {
            return get(list.get(0).getId(), null);
          }
        }
      };
    }

    context.eventDispatcher.registerListener(getMetaData().getObject() + Events.TYPE_CHANGED, listener);
  }
}
