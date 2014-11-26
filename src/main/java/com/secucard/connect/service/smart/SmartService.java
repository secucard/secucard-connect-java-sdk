package com.secucard.connect.service.smart;

import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.smart.Device;
import com.secucard.connect.model.smart.Ident;
import com.secucard.connect.model.smart.Result;
import com.secucard.connect.model.smart.Transaction;
import com.secucard.connect.model.transport.InvocationResult;
import com.secucard.connect.service.AbstractService;

import java.util.List;

/**
 * The Smart Product operations.
 */
public class SmartService extends AbstractService {
  /**
   * Register a device.
   *
   * @param device The device to register.
   * @return True if successfully, false else.
   */
  public boolean registerDevice(Device device) {
    // todo: switch to id, static just for test
    try {
      InvocationResult result = getStompChannel().execute("register", new String[]{"me"}, device, InvocationResult.class);
      return Boolean.parseBoolean(result.getResult());
    } catch (Exception e) {
      handleException(e);
    }
    return false;
  }

  /**
   * Returns all idents in the system or null if nothing found.
   */
  public List<Ident> getIdents() {
    try {
      ObjectList<Ident> idents = getChannnel().findObjects(Ident.class, null);
      if (idents != null) {
        return idents.getList();
      }
    } catch (Exception e) {
      handleException(e);
    }
    return null;
  }

  /**
   * Creating a transaction.
   *
   * @param transaction The transaction data to save.
   * @return The new transaction. Use this instance for further processing rather the the provided..
   */
  public Transaction createTransaction(Transaction transaction) {
    try {
      return getChannnel().saveObject(transaction);
    } catch (Exception e) {
      handleException(e);
    }
    return null;
  }

  /**
   * Starting/Exceuting a transaction.
   *
   * @param transaction The transaction data.
   * @param type
   * @return The result data.
   */
  public Result startTransaction(Transaction transaction, String type) {
    try {
      String[] id = {transaction.getId(), type};
      return getChannnel().execute("start", id, transaction, Result.class);
    } catch (Exception e) {
      handleException(e);
    }
    return null;
  }
}
