package com.secucard.connect.service.payment;

import com.secucard.connect.Callback;
import com.secucard.connect.ServerErrorException;
import com.secucard.connect.event.AbstractEventListener;
import com.secucard.connect.event.DelegatingEventHandlerCallback;
import com.secucard.connect.event.Events;
import com.secucard.connect.model.general.Event;
import com.secucard.connect.model.payment.SecupayPrepay;
import com.secucard.connect.model.transport.Result;
import com.secucard.connect.service.AbstractService;

import java.util.List;

/**
 * Provides Payment/Secupayprepay product operations.
 */
public class SecupayPrepayService extends AbstractService {

  public SecupayPrepay getTransaction(String id, Callback<SecupayPrepay> callback) {
    return new ServiceTemplate().get(SecupayPrepay.class, id, callback);
  }

  /**
   * Create a secupay prepay transaction.
   *
   * @param data     The prepay data.
   * @param callback Callback for async result processing.
   * @return The created transaction.
   */
  public SecupayPrepay createPrepay(SecupayPrepay data, Callback<SecupayPrepay> callback) {
    return new ServiceTemplate().create(data, callback);
  }

  /**
   * Cancel an existing prepay transaction.
   *
   * @param id       The prepay object id.
   * @param callback Callback for async processing.
   * @return
   */
  public Boolean cancelTransaction(final String id, Callback<Boolean> callback) {
    return new ServiceTemplate().executeToBoolean(SecupayPrepay.class, id, "cancel", null, null, Result.class, callback);
  }

  public void onSecuPrepayChanged(Callback<SecupayPrepay> callback) {
    AbstractEventListener listener = null;

    if (callback != null) {
      listener = new DelegatingEventHandlerCallback<Event<List<SecupayPrepay>>, SecupayPrepay>(callback) {
        @Override
        public boolean accept(Event event) {
          return Events.TYPE_CHANGED.equals(event.getType()) && SecupayPrepay.OBJECT.equals(event.getTarget());
        }

        @Override
        protected SecupayPrepay process(Event<List<SecupayPrepay>> event) {
          List<SecupayPrepay> list = event.getData();
          if (list == null || list.size() == 0) {
            throw new ServerErrorException("Invalid event data, prepay id not found.");
          } else {
            return getTransaction(list.get(0).getId(), null);
          }
        }
      };
    }

    getEventDispatcher().registerListener(SecupayPrepay.OBJECT + Events.TYPE_CHANGED, listener);
  }
}
