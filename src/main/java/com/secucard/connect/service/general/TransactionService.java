package com.secucard.connect.service.general;

import com.secucard.connect.Callback;
import com.secucard.connect.event.AbstractEventListener;
import com.secucard.connect.event.DelegatingEventHandlerCallback;
import com.secucard.connect.event.Events;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.QueryParams;
import com.secucard.connect.model.general.Event;
import com.secucard.connect.model.general.Transaction;
import com.secucard.connect.service.AbstractService;

import java.util.Map;

public class TransactionService extends AbstractService {

  public void onTransactionsChanged(final Callback<Transaction> callback) {
    AbstractEventListener listener = null;

    if (callback != null) {
      listener = new DelegatingEventHandlerCallback<Event, Transaction>(callback) {
        @Override
        public boolean accept(Event event) {
          return Events.TYPE_ADDED.equals(event.getType()) && Transaction.OBJECT.equals(event.getTarget());
        }

        @Override
        protected Transaction process(Event event) {
          String id = ((Map) event.getData()).get("id").toString();
          return getTransaction(id, this);
        }
      };
    }

    getEventDispatcher().registerListener(Transaction.OBJECT + Events.TYPE_ADDED, listener);
  }

  /**
   * Return a list of transactions
   *
   * @param queryParams Query params to find the wanted transactions
   * @return A list of found transactions
   */
  public ObjectList<Transaction> getTransactions(QueryParams queryParams, final Callback<ObjectList<Transaction>> callback) {
    return new ServiceTemplate().getList(Transaction.class, queryParams, callback);
  }

  public Transaction getTransaction(String pid, final Callback<Transaction> callback) {
    return new ServiceTemplate().get(Transaction.class, pid, callback);
  }
}
