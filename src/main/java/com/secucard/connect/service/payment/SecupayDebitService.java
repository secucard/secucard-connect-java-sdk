package com.secucard.connect.service.payment;

import com.secucard.connect.Callback;
import com.secucard.connect.SecuException;
import com.secucard.connect.event.AbstractEventListener;
import com.secucard.connect.event.DelegatingEventHandlerCallback;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.event.Events;
import com.secucard.connect.model.general.Event;
import com.secucard.connect.model.payment.SecupayDebit;
import com.secucard.connect.model.transport.Result;
import com.secucard.connect.service.AbstractService;

import java.util.List;

/**
 * Provides Payment/Secupaydebit product operations.
 */
public class SecupayDebitService extends AbstractService {

  public SecupayDebit getTransaction(String id, Callback<SecupayDebit> callback) {
    return new ServiceTemplate().get(SecupayDebit.class, id, callback);
  }

  /**
   * Create a secupay debit transaction.
   *
   * @param data     The debit data.
   * @param callback Callback for async result processing.
   * @return The created transaction.
   */
  public SecupayDebit createTransaction(SecupayDebit data, Callback<SecupayDebit> callback) {
    return new ServiceTemplate().create(data, callback);
  }

  /**
   * Cancel an existing transaction.
   *
   * @param id       The debit object id.
   * @param callback Callback for async processing.
   * @return
   */
  public Boolean cancelTransaction(final String id, Callback<Boolean> callback) {
    return new ServiceTemplate().executeToBoolean(SecupayDebit.class, id, "cancel", null, null, Result.class, callback);
  }

  public void onSecupayDebitChanged(Callback<SecupayDebit> callback) {
    AbstractEventListener listener = null;

    if (callback != null) {
      listener = new DelegatingEventHandlerCallback<Event<List<SecupayDebit>>, SecupayDebit>(callback) {

        @Override
        public boolean accept(Event event) {
          return Events.TYPE_CHANGED.equals(event.getType()) && SecupayDebit.OBJECT.equals(event.getTarget());
        }

        @Override
        protected SecupayDebit process(Event<List<SecupayDebit>> event) {
          List<SecupayDebit> list = event.getData();
          if (list == null || list.size() == 0) {
            throw new SecuException("Invalid event data, debit id not found.");
          } else {
            return getTransaction(list.get(0).getId(), null);
          }
        }
      };
    }

    context.getEventDispatcher().registerListener(SecupayDebit.OBJECT + Events.TYPE_CHANGED, listener);
  }

}
