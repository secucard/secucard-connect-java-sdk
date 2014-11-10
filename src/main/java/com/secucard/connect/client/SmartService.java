package com.secucard.connect.client;

import com.secucard.connect.event.EventListener;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.smart.Device;
import com.secucard.connect.model.smart.Ident;
import com.secucard.connect.model.smart.Result;
import com.secucard.connect.model.smart.Transaction;

import java.util.List;

public class SmartService extends AbstractService {
  private ClientContext context;

  @Override
  public void setEventListener(EventListener eventListener) {
    context.getStompChannel().setEventListener(eventListener);
  }

  @Override
  public void setContext(ClientContext context) {
    this.context = context;
  }

  public boolean registerDevice(Device device) {
    return context.getStompChannel().execute("register", new String[]{device.getId()}, device, null);
  }

  public List<Ident> getIdents() {
    ObjectList<Ident> idents = context.getChannnel().findObjects(Ident.class, null);
    if (idents != null) {
      return idents.getList();
    }
    return null;
  }

  public Transaction createTransaction(Transaction transaction) {
    return context.getChannnel().saveObject(transaction);
  }

  public Result startTransaction(Transaction transaction) {
    return context.getChannnel().execute("start", new String[]{transaction.getId(), "demo"}, transaction, Result.class);
  }
}
