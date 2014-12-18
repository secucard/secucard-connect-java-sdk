package com.secucard.connect.service.smart;

import com.secucard.connect.Callback;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.smart.Ident;
import com.secucard.connect.service.AbstractService;

import java.util.List;

public class IdentService extends AbstractService {

  /**
   * Returns all idents in the system or null if nothing found.
   */
  public List<Ident> getIdents(Callback<List<Ident>> callback) {
    return new ConvertingInvoker<ObjectList<Ident>, List<Ident>>() {
      @Override
      protected List<Ident> convert(ObjectList<Ident> value) {
        return value == null ? null : value.getList();
      }

      @Override
      protected ObjectList<Ident> handle(Callback<ObjectList<Ident>> callback) throws Exception {
        return getChannel().findObjects(Ident.class, null, callback);
      }
    }.invokeAndConvert(callback);
  }
}
