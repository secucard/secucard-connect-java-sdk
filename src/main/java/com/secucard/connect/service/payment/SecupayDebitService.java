package com.secucard.connect.service.payment;

import com.secucard.connect.Callback;
import com.secucard.connect.model.payment.SecupayDebit;
import com.secucard.connect.service.AbstractService;

public class SecupayDebitService extends AbstractService {

  /**
   * Create a transaction.
   *
   * @param data     The transaction data.
   * @param callback Callback for async result processing.
   * @return The created transaction.
   */
  public SecupayDebit createTransaction(SecupayDebit data, Callback<SecupayDebit> callback) {
    return create(data, callback, null);
  }
}
