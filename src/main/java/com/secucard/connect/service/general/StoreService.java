package com.secucard.connect.service.general;

import com.secucard.connect.Callback;
import com.secucard.connect.model.MediaResource;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.QueryParams;
import com.secucard.connect.model.general.Store;
import com.secucard.connect.model.transport.Result;
import com.secucard.connect.service.AbstractService;

import java.util.Arrays;
import java.util.List;

public class StoreService extends AbstractService {

  /**
   * Set checkIn state for the store
   *
   * @param storeId StoreID
   * @return True if successfully updated, false else.
   */
  public boolean checkIn(final String storeId, final String sid, Callback<Boolean> callback) {
    return new ServiceTemplate().executeToBoolean(Store.class, storeId, "checkin", sid, null, Result.class, callback);
  }

  /**
   * Set store as default
   *
   * @param storeId StoreID
   * @return True if successfully updated, false else.
   */
  public boolean setDefault(final String storeId, Callback<Boolean> callback) {
    return new ServiceTemplate().executeToBoolean(Store.class, storeId, "setDefault", null, null, Result.class,
        callback);
  }

  /**
   * Return a list of stores
   *
   * @param queryParams Query params to find the wanted stores
   * @return A list of found stores
   */
  public ObjectList<Store> getStores(QueryParams queryParams, final Callback<ObjectList<Store>> callback) {
    return new ServiceTemplate() {
      @Override
      protected void onResult(Object arg) {
        ObjectList<Store> stores = (ObjectList<Store>) arg;
        if (stores != null && stores.getList() != null) {
          processStore(stores.getList());
        }
      }
    }.getList(Store.class, queryParams, callback);
  }

  public Store getStore(String pid, QueryParams queryParams, Callback<Store> callback) {
    return new ServiceTemplate() {
      @Override
      protected void onResult(Object arg) {
        Store store = (Store) arg;
        if (store != null) {
          processStore(Arrays.asList(store));
        }
      }
    }.get(Store.class, pid, callback);
  }

  private void processStore(List<Store> stores) {
    setContext();
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
