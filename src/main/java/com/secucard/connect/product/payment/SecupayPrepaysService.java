package com.secucard.connect.product.payment;

import com.secucard.connect.client.Callback;
import com.secucard.connect.client.ProductService;
import com.secucard.connect.client.SecucardConnectException;
import com.secucard.connect.event.AbstractEventListener;
import com.secucard.connect.event.DelegatingEventHandlerCallback;
import com.secucard.connect.event.Events;
import com.secucard.connect.product.general.model.Event;
import com.secucard.connect.product.payment.model.SecupayPrepay;

import java.util.List;

/**
 * Provides Payment/Secupayprepay product operations.
 */
public class SecupayPrepaysService extends ProductService<SecupayPrepay> {

  @Override
  protected ServiceMetaData<SecupayPrepay> createMetaData() {
    return new ServiceMetaData<>("payment", "secupayprepay", SecupayPrepay.class);
  }

  /**
   * Cancel an existing prepay transaction.
   *
   * @param id       The prepay object id.
   * @param callback Callback for async processing.
   * @return
   */
  public Boolean cancelTransaction(final String id, Callback<Boolean> callback) {
    return executeToBool(id, "cancel", null, null, callback);
  }

  public void onSecuPrepayChanged(Callback<SecupayPrepay> callback) {
    AbstractEventListener listener = null;

    if (callback != null) {
      listener = new DelegatingEventHandlerCallback<List<SecupayPrepay>, SecupayPrepay>(callback) {
        @Override
        public boolean accept(Event event) {
          return Events.TYPE_CHANGED.equals(event.getType()) && getMetaData().getObject().equals(event.getTarget());
        }

        @Override
        protected SecupayPrepay process(Event<List<SecupayPrepay>> event) {
          List<SecupayPrepay> list = event.getData();
          if (list == null || list.size() == 0) {
            throw new SecucardConnectException("Invalid event data, prepay id not found.");
          } else {
            return get(list.get(0).getId(), null);
          }
        }
      };
    }

    context.eventDispatcher.registerListener(getMetaData().getObject() + Events.TYPE_CHANGED, listener);
  }
}
