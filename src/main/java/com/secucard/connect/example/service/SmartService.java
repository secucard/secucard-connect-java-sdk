package com.secucard.connect.example.service;

import com.secucard.connect.client.BaseClient;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.smart.Device;
import com.secucard.connect.model.smart.Ident;
import com.secucard.connect.model.smart.Result;
import com.secucard.connect.model.smart.Transaction;

import java.util.List;

public class SmartService extends BaseClient {

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
