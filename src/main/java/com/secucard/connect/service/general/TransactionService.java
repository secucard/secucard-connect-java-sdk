package com.secucard.connect.service.general;

import com.secucard.connect.Callback;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.QueryParams;
import com.secucard.connect.model.general.Transaction;
import com.secucard.connect.service.AbstractService;

public class TransactionService extends AbstractService {

  /**
   * Return a list of transactions
   *
   * @param queryParams Query params to find the wanted transactions
   * @return A list of found transactions
   */
  public ObjectList<Transaction> getStores(QueryParams queryParams, final Callback<ObjectList<Transaction>> callback) {
    try {
      ObjectList<Transaction> objects = getRestChannel().findObjects(Transaction.class, queryParams,
              callback);
      return objects;
    } catch (Exception e) {
      handleException(e, callback);
    }
    return null;
  }
}
