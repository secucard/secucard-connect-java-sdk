package com.secucard.connect.service.general;

import com.secucard.connect.Callback;
import com.secucard.connect.event.EventHandler;
import com.secucard.connect.event.Events;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.QueryParams;
import com.secucard.connect.model.general.AccountDevice;
import com.secucard.connect.model.general.Event;
import com.secucard.connect.model.general.Transaction;
import com.secucard.connect.service.AbstractService;

import java.util.Map;

public class TransactionService extends AbstractService {

  public static final String ID = Transaction.OBJECT + Events.TYPE_ADDED;

  public void onTransactionsChanged(final Callback<Transaction> callback) {
    addOrRemoveEventHandler(ID, new TransactionsEventEventHandler(callback), callback);
  }

  private class TransactionsEventEventHandler extends EventHandler<Transaction, Event> {
    public TransactionsEventEventHandler(Callback<Transaction> callback) {
      super(callback);
    }

    @Override
    public boolean accept(Event event) {
      return Events.TYPE_ADDED.equals(event.getType()) && Transaction.OBJECT.equals(event.getTarget());
    }

    @Override
    public void handle(Event event) {
      String id = ((Map) event.getData()).get("id").toString();
      getTransaction(id, this);
    }
  }

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
