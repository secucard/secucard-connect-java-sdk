package com.secucard.connect;

import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.general.skeleton.Skeleton;
import com.secucard.connect.model.smart.Device;
import com.secucard.connect.model.smart.Ident;
import com.secucard.connect.model.smart.Result;
import com.secucard.connect.model.smart.Transaction;

import java.util.List;

/**
 * Global client API, implementing all operations at once.
 * For DEVELOPMENT!!! purposes.
 */
public class Client extends BaseClient {

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

  public Skeleton getSkeleton(String id){
    return selectChannnel().getObject(Skeleton.class, id);
  }

  public List<Skeleton> getSkeletons(){
    return selectChannnel().findObjects(Skeleton.class, null).getList();
  }


  public static Client create(ClientConfig config) {
    return  BaseClient.create(config, Client.class);
  }
}
