package com.secucard.connect.client;

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

  public Skeleton getSkeleton(String id){
    return context.getChannnel().getObject(Skeleton.class, id);
  }

  public List<Skeleton> getSkeletons(){
    return context.getChannnel().findObjects(Skeleton.class, null).getList();
  }


}
