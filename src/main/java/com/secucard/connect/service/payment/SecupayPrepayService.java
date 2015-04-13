package com.secucard.connect.service.payment;

import com.secucard.connect.Callback;
import com.secucard.connect.SecuException;
import com.secucard.connect.event.EventHandler;
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
    return get(SecupayPrepay.class, id, callback, null);
  }

  /**
   * Create a secupay prepay transaction.
   *
   * @param data     The prepay data.
   * @param callback Callback for async result processing.
   * @return The created transaction.
   */
  public SecupayPrepay createPrepay(SecupayPrepay data, Callback<SecupayPrepay> callback) {
    return super.create(data, callback, null);
  }

  /**
   * Cancel an existing prepay transaction.
   *
   * @param id       The prepay object id.
   * @param callback Callback for async processing.
   * @return
   */
  public Boolean cancelTransaction(final String id, Callback<Boolean> callback) {
    return new Result2BooleanInvoker() {
      @Override
      protected Result handle(Callback<Result> callback) throws Exception {
        return execute(SecupayPrepay.class, id, "cancel", null, null, Result.class, callback, null);
      }
    }.invokeAndConvert(callback);
  }

  public void onPrepayChanged(Callback<SecupayPrepay> callback) {
    addOrRemoveEventHandler(SecupayPrepay.OBJECT + Events.TYPE_CHANGED, new SecuPrepayChangedEventHandler(callback), callback);
  }

  private class SecuPrepayChangedEventHandler extends EventHandler<SecupayPrepay, Event> {
    public SecuPrepayChangedEventHandler(Callback<SecupayPrepay> callback) {
      super(callback);
    }

    @Override
    public boolean accept(Event event) {
      return Events.TYPE_CHANGED.equals(event.getType()) && SecupayPrepay.OBJECT.equals(event.getTarget());
    }

    @Override
    public void handle(Event event) {
      List<SecupayPrepay> list = (List<SecupayPrepay>) event.getData();
      if (list == null || list.size() == 0) {
        failed(new SecuException("Invalid event data, prepay id not found."));
      } else {
        getTransaction(list.get(0).getId(), this);
      }
    }
  }
}
