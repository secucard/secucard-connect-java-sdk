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
  public ObjectList<Transaction> getTransactions(QueryParams queryParams, final Callback<ObjectList<Transaction>> callback) {
    try {
      return getRestChannel().findObjects(Transaction.class, queryParams,
              callback);
    } catch (Exception e) {
      handleException(e, callback);
    }
    return null;
  }

  public Transaction getTransaction(String pid, final Callback<Transaction> callback) {
    try {
      return getRestChannel().getObject(Transaction.class, pid,
              callback);
    } catch (Exception e) {
      handleException(e, callback);
    }
    return null;
  }
}
