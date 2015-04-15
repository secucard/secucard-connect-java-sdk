package com.secucard.connect.service.payment;

import com.secucard.connect.Callback;
import com.secucard.connect.SecuException;
import com.secucard.connect.event.AbstractEventHandler;
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
    return get(SecupayDebit.class, id, callback, null);
  }

  /**
   * Create a secupay debit transaction.
   *
   * @param data     The debit data.
   * @param callback Callback for async result processing.
   * @return The created transaction.
   */
  public SecupayDebit createTransaction(SecupayDebit data, Callback<SecupayDebit> callback) {
    return super.create(data, callback, null);
  }

  /**
   * Cancel an existing transaction.
   *
   * @param id       The debit object id.
   * @param callback Callback for async processing.
   * @return
   */
  public Boolean cancelTransaction(final String id, Callback<Boolean> callback) {
    return new Result2BooleanInvoker() {
      @Override
      protected Result handle(Callback<Result> callback) throws Exception {
        return execute(SecupayDebit.class, id, "cancel", null, null, Result.class, callback, null);
      }
    }.invokeAndConvert(callback);
  }

  public void onSecupayDebitChanged(SecuDebitChangedEventHandler handler) {
    if (handler != null) {
      handler.setService(this);
    }
    addOrRemoveEventHandler(SecupayDebit.OBJECT + Events.TYPE_CHANGED, handler);
  }

  public static abstract class SecuDebitChangedEventHandler extends AbstractEventHandler<SecupayDebit, Event> {
    private SecupayDebitService service;

    public void setService(SecupayDebitService service) {
      this.service = service;
    }

    @Override
    public boolean accept(Event event) {
      return Events.TYPE_CHANGED.equals(event.getType()) && SecupayDebit.OBJECT.equals(event.getTarget());
    }

    @Override
    public void handle(Event event) {
      List<SecupayDebit> list = (List<SecupayDebit>) event.getData();
      if (list == null || list.size() == 0) {
        SecuException exception = new SecuException("Invalid event data, debit id not found.");
        if (isAsync()) {
          failed(exception);
        } else {
          throw exception;
        }
      } else {

        if (isAsync()) {
          service.getTransaction(list.get(0).getId(), new Callback<SecupayDebit>() {
            @Override
            public void completed(SecupayDebit result) {
              SecuDebitChangedEventHandler.this.completed(result);
            }

            @Override
            public void failed(Throwable cause) {
              SecuDebitChangedEventHandler.this.failed(cause);
            }
          });
        } else {
          SecuDebitChangedEventHandler.this.completed(service.getTransaction(list.get(0).getId(), null));
        }
      }
    }
  }
}
