package com.secucard.connect.service.payment;

import com.secucard.connect.Callback;
import com.secucard.connect.model.payment.SecupayPrepay;
import com.secucard.connect.model.transport.Result;
import com.secucard.connect.service.AbstractService;

/**
 * Provides Payment/Secupayprepay product operations.
 */
public class SecupayPrepayService extends AbstractService {

  /**
   * Create a secupay pre pay transaction.
   *
   * @param data     The prepay data.
   * @param callback Callback for async result processing.
   * @return The created transaction.
   */
  public SecupayPrepay createPrepay(SecupayPrepay data, Callback<SecupayPrepay> callback) {
    return super.create(data, callback, null);
  }

  /**
   * Cancel an existing pre pay transaction.
   *
   * @param id       The pre pay object id.
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
}
