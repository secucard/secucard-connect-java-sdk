package com.secucard.connect.product.smart;

import com.secucard.connect.client.Callback;
import com.secucard.connect.client.ProductService;
import com.secucard.connect.net.Options;
import com.secucard.connect.product.common.model.ObjectList;
import com.secucard.connect.product.smart.model.Ident;

import java.util.List;

public class IdentService extends ProductService<Ident> {

  @Override
  protected ServiceMetaData<Ident> createMetaData() {
    return new ServiceMetaData<>("smart", "idents", Ident.class);
  }

  @Override
  public Options getDefaultOptions() {
    return new Options(Options.CHANNEL_STOMP);
  }

  /**
   * Returns all idents in the system or null if nothing found.
   */
  public ObjectList<Ident> getList(Callback<ObjectList<Ident>> callback) {
    return super.getList(null, null, callback);
  }

  public List<Ident> getSimpleList(Callback<List<Ident>> callback) {
    return super.getSimpleList(null, null, callback);
  }

  public Ident readIdent(String id, Callback<Ident> callback) {
    return super.execute(id, "read", null, null, Ident.class, null, callback);
  }
}
