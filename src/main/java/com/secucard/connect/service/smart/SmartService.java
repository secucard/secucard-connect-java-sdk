package com.secucard.connect.service.smart;

import com.secucard.connect.Callback;
import com.secucard.connect.CallbackAdapter;
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
  public boolean registerDevice(Device device, Callback callback) {
    CallbackAdapter<InvocationResult, Boolean> adapter = null;
    if (callback != null) {
      adapter = new CallbackAdapter<InvocationResult, Boolean>(callback) {
        @Override
        protected Boolean convert(InvocationResult object) {
          return Boolean.parseBoolean(object.getResult());
        }
      };
    }
    // todo: switch to id, static just for test
    try {
      InvocationResult result = getStompChannel().execute("register", "me", null, device, InvocationResult.class,
          adapter);
      return Boolean.parseBoolean(result.getResult());
    } catch (Exception e) {
      handleException(e);
    }
    return false;
  }

  /**
   * Returns all idents in the system or null if nothing found.
   */
  public List<Ident> getIdents(Callback<List<Ident>> callback) {

    CallbackAdapter<ObjectList<Ident>, List<Ident>> adapter = null;
    if (callback != null) {
      adapter = new CallbackAdapter<ObjectList<Ident>, List<Ident>>(callback) {
        @Override
        protected List<Ident> convert(ObjectList<Ident> object) {
          return object.getList();
        }
      };
    }

    try {
      ObjectList<Ident> idents = getChannnel().findObjects(Ident.class, null, adapter);
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
  public Transaction createTransaction(Transaction transaction, Callback<Transaction> callback) {
    try {
      return getChannnel().saveObject(transaction, callback);
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
  public Result startTransaction(Transaction transaction, String type, Callback<Result> callback) {
    try {
      return getChannnel().execute("start", transaction.getId(), type, transaction, Result.class, callback);
    } catch (Exception e) {
      handleException(e);
    }
    return null;
  }
}
