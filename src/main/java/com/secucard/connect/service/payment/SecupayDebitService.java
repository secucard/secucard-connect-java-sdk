package com.secucard.connect.service.payment;

import com.secucard.connect.Callback;
import com.secucard.connect.model.payment.SecupayDebit;
import com.secucard.connect.model.transport.Result;
import com.secucard.connect.service.AbstractService;

/**
 * Provides Payment/Secupaydebit product operations.
 */
public class SecupayDebitService extends AbstractService {

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
}
