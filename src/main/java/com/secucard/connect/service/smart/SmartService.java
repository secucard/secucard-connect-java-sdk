package com.secucard.connect.service.smart;

import com.secucard.connect.Callback;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.smart.Device;
import com.secucard.connect.model.smart.Ident;
import com.secucard.connect.model.smart.Result;
import com.secucard.connect.model.smart.Transaction;
import com.secucard.connect.model.transport.InvocationResult;
import com.secucard.connect.service.AbstractService;
import com.secucard.connect.util.Converter;

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
    try {
      Converter<InvocationResult, Boolean> converter = new Converter<InvocationResult, Boolean>() {
        @Override
        public Boolean convert(InvocationResult value) {
          return value == null ? Boolean.FALSE : Boolean.parseBoolean(value.getResult());
        }
      };
      // todo: switch to id, static just for test
      InvocationResult result = getStompChannel().execute("register", "me", null, device, InvocationResult.class,
          getCallbackAdapter(callback, converter));
      return converter.convert(result);
    } catch (Exception e) {
      handleException(e, callback);
    }
    return false;
  }

  /**
   * Returns all idents in the system or null if nothing found.
   */
  public List<Ident> getIdents(Callback<List<Ident>> callback) {


    try {
      Converter<ObjectList<Ident>, List<Ident>> converter = new Converter<ObjectList<Ident>, List<Ident>>() {
        @Override
        public List<Ident> convert(ObjectList<Ident> value) {
          return value == null ? null : value.getList();
        }
      };
      ObjectList<Ident> idents = getChannnel().findObjects(Ident.class, null, getCallbackAdapter(callback, converter));
      return converter.convert(idents);
    } catch (Exception e) {
      handleException(e, callback);
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
      handleException(e, callback);
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
      handleException(e, callback);
    }
    return null;
  }
}
