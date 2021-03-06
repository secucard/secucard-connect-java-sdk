package com.secucard.connect.product.payment;

import com.secucard.connect.client.Callback;
import com.secucard.connect.client.ClientError;
import com.secucard.connect.client.ProductService;
import com.secucard.connect.event.AbstractEventListener;
import com.secucard.connect.event.DelegatingEventHandlerCallback;
import com.secucard.connect.event.Events;
import com.secucard.connect.product.general.model.Event;
import com.secucard.connect.product.payment.model.CancelResponse;
import com.secucard.connect.product.payment.model.SecupayPrepay;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implements the payment/secupayprepay operations.
 */
public class SecupayPrepaysService extends ProductService<SecupayPrepay> {

  public static final ServiceMetaData<SecupayPrepay> META_DATA = new ServiceMetaData<>("payment", "secupayprepays", SecupayPrepay.class);

  @Override
  public ServiceMetaData<SecupayPrepay> getMetaData() {
    return META_DATA;
  }

  /**
   * Cancel an existing transaction.
   *
   * @param id The prepay transaction id.
   * @param contractId The id of the contract that was used to create this transaction. May be null if the contract is an parent contract (not
   * cloned).
   * @param callback Callback for async processing.
   * @return True if successful, false else.
   */
  public Boolean cancel(final String id, final String contractId, Callback<CancelResponse> callback) {
    Map<String, String> map = new HashMap<>();
    map.put("contract", contractId == null ? "" : contractId);
    CancelResponse result = execute(id, "cancel", null, map, CancelResponse.class, null, callback);
    return true;
  }

  /**
   * Cancel an existing transaction.
   *
   * @param id The prepay transaction id.
   * @param contractId The id of the contract that was used to create this transaction. May be null if the contract is an parent contract (not
   * cloned).
   * @return True if successful, false else.
   */
  public Boolean cancel(final String id, final String contractId) {
    return cancel(id, contractId, null);
  }

  /**
   * Cancel an existing transaction.
   *
   * @param id The prepay transaction id.
   * @return True if successful, false else.
   */
  public Boolean cancel(final String id) {
    return cancel(id, null, null);
  }

  /**
   * Set a callback to get notified when a prepay has changed.
   */
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
            throw new ClientError("Invalid event data, prepay id not found.");
          } else {
            return get(list.get(0).getId(), null);
          }
        }
      };
    }

    context.eventDispatcher.registerListener(getMetaData().getObject() + Events.TYPE_CHANGED, listener);
  }
}
