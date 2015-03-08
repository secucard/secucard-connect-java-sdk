package com.secucard.connect.service.smart;

import com.secucard.connect.Callback;
import com.secucard.connect.ClientContext;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.smart.Ident;
import com.secucard.connect.service.AbstractService;

import java.util.List;

public class IdentService extends AbstractService {

  /**
   * Returns all idents in the system or null if nothing found.
   */
  public ObjectList<Ident> getIdentsList(Callback<ObjectList<Ident>> callback) {
    return getObjectList(Ident.class, null, callback, ClientContext.STOMP);
  }

  public List<Ident> getIdents(Callback<List<Ident>> callback) {
    return getList(Ident.class, null, callback, ClientContext.STOMP);
  }
}
