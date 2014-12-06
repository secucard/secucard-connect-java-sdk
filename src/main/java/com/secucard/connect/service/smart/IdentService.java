package com.secucard.connect.service.smart;

import com.secucard.connect.Callback;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.smart.Ident;
import com.secucard.connect.service.AbstractService;
import com.secucard.connect.util.Converter;

import java.util.List;

public class IdentService extends AbstractService {

  /**
   * Returns all idents in the system or null if nothing found.
   */
  public List<Ident> getIdents(Callback<List<Ident>> callback) {


    try {
      Converter<ObjectList<Ident>, List<Ident>> converter = new Converter<ObjectList<Ident>, List<Ident>>() {
        @Override
        public List<Ident> convert(ObjectList<Ident> value) {
          return value == null ? null : value.getList();
        }
      };
      ObjectList<Ident> idents = getChannnel().findObjects(Ident.class, null, getCallbackAdapter(callback, converter));
      return converter.convert(idents);
    } catch (Exception e) {
      handleException(e, callback);
    }
    return null;
  }
}
