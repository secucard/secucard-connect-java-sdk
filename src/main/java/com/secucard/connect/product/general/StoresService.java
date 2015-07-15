package com.secucard.connect.product.general;

import com.secucard.connect.client.Callback;
import com.secucard.connect.client.ProductService;
import com.secucard.connect.net.Options;
import com.secucard.connect.product.common.model.MediaResource;
import com.secucard.connect.product.common.model.ObjectList;
import com.secucard.connect.product.common.model.QueryParams;
import com.secucard.connect.product.general.model.Store;

import java.util.Arrays;
import java.util.List;

public class StoresService extends ProductService<Store> {

  @Override
  public ServiceMetaData<Store> createMetaData() {
    return new ServiceMetaData<>("general", "stores", Store.class);
  }

  /**
   * Set checkIn state for the store
   *
   * @param storeId StoreID
   * @return True if successfully updated, false else.
   */
  public boolean checkIn(final String storeId, final String sid, Callback<Boolean> callback) {
    return super.executeToBool(storeId, "checkin", sid, null, callback);
  }

  /**
   * Set store as default
   *
   * @param storeId StoreID
   * @return True if successfully updated, false else.
   */
  public boolean setDefault(final String storeId, Callback<Boolean> callback) {
    return super.executeToBool(storeId, "setDefault", null, null, callback);
  }

  /**
   * Return a list of stores
   *
   * @param queryParams Query params to find the wanted stores
   * @return A list of found stores
   */
  public ObjectList<Store> getList(QueryParams queryParams, final Callback<ObjectList<Store>> callback) {
    Options options = getDefaultOptions();
    options.resultProcessing = new Callback.Notify<ObjectList<Store>>() {
      @Override
      public void notify(ObjectList<Store> result) {
        if (result != null && result.getList() != null) {
          processStore(result.getList());
        }
      }
    };
    return getList(queryParams, options, callback);
  }

  public Store get(String pid, QueryParams queryParams, Callback<Store> callback) {
    Options options = getDefaultOptions();
    options.resultProcessing = new Callback.Notify<Store>() {
      @Override
      public void notify(Store result) {
        processStore(Arrays.asList(result));
      }
    };
    return get(pid, options, callback);
  }

  private void processStore(List<Store> stores) {
    for (Store object : stores) {
      MediaResource picture = object.getLogo();
      if (picture != null) {
        if (!picture.isCached()) {
          picture.download();
        }
      }
    }
  }
}
