package com.secucard.connect.example.service;

import com.secucard.connect.BaseClient;
import com.secucard.connect.ClientConfig;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.smart.Device;
import com.secucard.connect.model.smart.Ident;
import com.secucard.connect.model.smart.Result;
import com.secucard.connect.model.smart.Transaction;

import java.util.List;

public class SmartService extends BaseClient {

  public boolean registerDevice(Device device) {
    return selectChannnel(ChannelName.STOMP).execute("register", new String[]{device.getId()}, device, null);
  }

  public List<Ident> getIdents() {
    ObjectList<Ident> idents = selectChannnel().findObjects(Ident.class, null);
    if (idents != null) {
      return idents.getList();
    }
    return null;
  }

  public Transaction createTransaction(Transaction transaction) {
    return selectChannnel().saveObject(transaction);
  }


  public Result startTransaction(Transaction transaction) {
    return selectChannnel().execute("start", new String[]{transaction.getId(), "demo"}, transaction, Result.class);
  }

  public static SmartService create(ClientConfig config) {
    return BaseClient.create(config, SmartService.class);
  }

}
