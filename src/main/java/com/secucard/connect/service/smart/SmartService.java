package com.secucard.connect.service.smart;

import com.secucard.connect.Callback;
import com.secucard.connect.model.smart.Device;
import com.secucard.connect.model.smart.Ident;
import com.secucard.connect.model.smart.TransactionResult;
import com.secucard.connect.model.smart.Transaction;
import com.secucard.connect.service.AbstractService;

import java.util.List;

/**
 * Bundles all Smart Product operations.
 */
public class SmartService extends AbstractService {
  private DeviceService deviceService;
  private TransactionService transactionService;
  private IdentService identService;

  /**
   * Register a device.
   *
   * @param device The device to register.
   * @return True if successfully, false else.
   */
  public boolean registerDevice(Device device, Callback callback) {
    return deviceService.registerDevice(device, callback);
  }

  /**
   * Returns all idents in the system or null if nothing found.
   */
  public List<Ident> getIdents(Callback<List<Ident>> callback) {
    return identService.getIdents(callback);
  }

  /**
   * Creating a transaction.
   *
   * @param transaction The transaction data to save.
   * @return The new transaction. Use this instance for further processing rather the the provided..
   */
  public Transaction createTransaction(Transaction transaction, Callback<Transaction> callback) {
    return transactionService.createTransaction(transaction, callback);
  }

  /**
   * Starting/Exceuting a transaction.
   *
   * @param transaction The transaction data.
   * @param type
   * @return The result data.
   */
  public TransactionResult startTransaction(Transaction transaction, String type, Callback<TransactionResult> callback) {
    return transactionService.startTransaction(transaction, type, callback);
  }
}
