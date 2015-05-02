package com.secucard.connect.service.smart;

import com.secucard.connect.Callback;
import com.secucard.connect.channel.Channel;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.smart.Ident;
import com.secucard.connect.service.AbstractService;

import java.util.List;

public class IdentService extends AbstractService {

  /**
   * Returns all idents in the system or null if nothing found.
   */
  public ObjectList<Ident> getIdentsList(Callback<ObjectList<Ident>> callback) {
    return new ServiceTemplate(Channel.STOMP).getList(Ident.class, null, callback);
  }

  public List<Ident> getIdents(Callback<List<Ident>> callback) {
    return new ServiceTemplate(Channel.STOMP).getAsList(Ident.class, null, callback);
  }

  public Ident readIdent(String id, Callback<Ident> callback) {
    return new ServiceTemplate(Channel.STOMP).execute(Ident.class, id, "read", null, null, Ident.class, callback);
  }
}
