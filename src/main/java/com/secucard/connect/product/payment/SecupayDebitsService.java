package com.secucard.connect.product.payment;

import com.secucard.connect.client.Callback;
import com.secucard.connect.client.ClientError;
import com.secucard.connect.client.ProductService;
import com.secucard.connect.event.AbstractEventListener;
import com.secucard.connect.event.DelegatingEventHandlerCallback;
import com.secucard.connect.event.Events;
import com.secucard.connect.product.general.model.Event;
import com.secucard.connect.product.payment.model.SecupayDebit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implements the payment/secupaydebit operations.
 */
public class SecupayDebitsService extends ProductService<SecupayDebit> {

  public static final ServiceMetaData<SecupayDebit> META_DATA = new ServiceMetaData<>("payment",
      "secupaydebits", SecupayDebit.class);

  @Override
  public ServiceMetaData<SecupayDebit> getMetaData() {
    return META_DATA;
  }

  /**
   * Cancel an existing transaction.
   *
   * @param id The debit transaction id.
   * @param contractId The id of the contract that was used to create this transaction. May be null
   * if the contract is an parent contract (not cloned).
   * @param callback Callback for async processing.
   * @return True if successful, false else.
   */
  public Boolean cancel(final String id, final String contractId, Callback<Boolean> callback) {
    Map<String, String> map = new HashMap<>();
    map.put("contract", contractId == null ? "" : contractId);
    return executeToBool(id, "cancel", null, map, null, callback);
  }

  /**
   * Cancel an existing transaction.
   *
   * @param id The debit transaction id.
   * @param contractId The id of the contract that was used to create this transaction. May be null
   * if the contract is an parent contract (not cloned).
   * @return True if successful, false else.
   */
  public Boolean cancel(final String id, final String contractId) {
    return cancel(id, contractId, null);
  }

  /**
   * Cancel an existing transaction.
   *
   * @param id The debit transaction id.
   * @return True if successful, false else.
   */
  public Boolean cancel(final String id) {
    return cancel(id, null, null);
  }


  /**
   * Set a callback to get notified when a debit has changed.
   */
  public void onSecupayDebitChanged(Callback<SecupayDebit> callback) {
    AbstractEventListener listener = null;

    if (callback != null) {
      listener = new DelegatingEventHandlerCallback<List<SecupayDebit>, SecupayDebit>(callback) {

        @Override
        public boolean accept(Event event) {
          return Events.TYPE_CHANGED.equals(event.getType()) && getMetaData().getObject()
              .equals(event.getTarget());
        }

        @Override
        protected SecupayDebit process(Event<List<SecupayDebit>> event) {
          List<SecupayDebit> list = event.getData();
          if (list == null || list.size() == 0) {
            throw new ClientError("Invalid event data, missing debit ids.");
          } else {
            return get(list.get(0).getId(), null);
          }
        }
      };
    }

    context.eventDispatcher
        .registerListener(getMetaData().getObject() + Events.TYPE_CHANGED, listener);
  }
}
