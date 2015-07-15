package com.secucard.connect.product.general;

import com.secucard.connect.client.Callback;
import com.secucard.connect.client.ProductService;
import com.secucard.connect.event.AbstractEventListener;
import com.secucard.connect.event.DelegatingEventHandlerCallback;
import com.secucard.connect.event.Events;
import com.secucard.connect.product.general.model.Event;
import com.secucard.connect.product.general.model.Transaction;

import java.util.Map;

public class TransactionsService extends ProductService<Transaction> {

  @Override
  public ServiceMetaData<Transaction> createMetaData() {
    return new ServiceMetaData<>("general", "transactions", Transaction.class);
  }

  public void onTransactionsChanged(final Callback<Transaction> callback) {
    AbstractEventListener listener = null;

    if (callback != null) {
      listener = new DelegatingEventHandlerCallback<Event, Transaction>(callback) {
        @Override
        public boolean accept(Event event) {
          return Events.TYPE_ADDED.equals(event.getType()) && getMetaData().getObject().equals(event.getTarget());
        }

        @Override
        protected Transaction process(Event event) {
          String id = ((Map) event.getData()).get("id").toString();
          return get(id, this);
        }
      };
    }

    context.eventDispatcher.registerListener(getMetaData().getObject() + Events.TYPE_ADDED, listener);
  }
}
