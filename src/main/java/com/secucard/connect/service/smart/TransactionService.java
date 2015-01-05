package com.secucard.connect.service.smart;

import com.secucard.connect.Callback;
import com.secucard.connect.model.smart.Transaction;
import com.secucard.connect.model.smart.TransactionResult;
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
  public Transaction createTransaction(final Transaction transaction, Callback<Transaction> callback) {
    return new Invoker<Transaction>(){
      @Override
      protected Transaction handle(Callback<Transaction> callback) throws Exception {
        return getChannel().createObject(transaction, callback);
      }
    }.invoke(callback);
  }

  /**
   * Starting/Exceuting a transaction.
   *
   * @param transactionId The transaction id.
   * @param type
   * @return The result data.
   */
  public TransactionResult startTransaction(final String transactionId, final String type,
                                            Callback<TransactionResult> callback) {
    return new Invoker<TransactionResult>(){
      @Override
      protected TransactionResult handle(Callback<TransactionResult> callback) throws Exception {
        return getChannel().execute(Transaction.class, transactionId, "start", type, null, TransactionResult.class,
            callback);
      }
    }.invoke(callback);
  }
}
