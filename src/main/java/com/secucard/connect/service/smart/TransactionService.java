package com.secucard.connect.service.smart;

import com.secucard.connect.Callback;
import com.secucard.connect.model.smart.TransactionResult;
import com.secucard.connect.model.smart.Transaction;
import com.secucard.connect.service.AbstractService;

/**
 * The Smart Product operations.
 */
public class TransactionService extends AbstractService {

  /**
   * Creating a transaction.
   *
   * @param transaction The transaction data to save.
   * @return The new transaction. Use this instance for further processing rather the the provided..
   */
  public Transaction createTransaction(Transaction transaction, Callback<Transaction> callback) {
    try {
      return getChannel().createObject(transaction, callback);
    } catch (Exception e) {
      handleException(e, callback);
    }
    return null;
  }

  /**
   * Starting/Exceuting a transaction.
   *
   * @param transactionId The transaction id.
   * @param type
   * @return The result data.
   */
  public TransactionResult startTransaction(String transactionId, String type, Callback<TransactionResult> callback) {
    try {
      return getChannel().execute(Transaction.class, transactionId, "start", type, null, TransactionResult.class, callback);
    } catch (Exception e) {
      handleException(e, callback);
    }
    return null;
  }
}
