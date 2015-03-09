package com.secucard.connect.service.general;

import com.secucard.connect.Callback;
import com.secucard.connect.ClientContext;
import com.secucard.connect.model.MediaResource;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.QueryParams;
import com.secucard.connect.model.general.Store;
import com.secucard.connect.model.smart.Checkin;
import com.secucard.connect.model.transport.Result;
import com.secucard.connect.service.AbstractService;

import java.util.List;

public class StoreService extends AbstractService {

  /**
   * Set checkIn state for the store
   *
   * @param storeId StoreID
   * @return True if successfully updated, false else.
   */
  public boolean checkIn(final String storeId, final String sid, Callback<Boolean> callback) {
    return new Result2BooleanInvoker() {
      @Override
      protected Result handle(Callback<Result> callback) throws Exception {
        return getStompChannel().execute(Store.class, storeId, "checkin", sid, null, Result.class, callback);
      }
    }.invokeAndConvert(callback);
  }

  /**
   * Set store as default
   *
   * @param storeId StoreID
   * @return True if successfully updated, false else.
   */
  public boolean setDefault(final String storeId, Callback<Boolean> callback) {
    return getRestChannel().execute(Store.class, storeId, "setDefault", null, null, Boolean.class, callback);
  }

  /**
   * Return a list of stores
   *
   * @param queryParams Query params to find the wanted stores
   * @return A list of found stores
   */
  public ObjectList<Store> getStores(QueryParams queryParams, final Callback<ObjectList<Store>> callback) {
    try {
      ObjectList<Store> objects = getRestChannel().findObjects(Store.class, queryParams,
              callback);
      return objects;
    } catch (Exception e) {
      handleException(e, callback);
    }
    return null;
  }

  public List<Store> getStoreList(QueryParams queryParams, final Callback<List<Store>> callback) {
    return getList(Store.class, queryParams, callback, ClientContext.REST);
  }

  public Store getStore(String pid, QueryParams queryParams, Callback<Store> callback) {
    return getRestChannel().getObject(Store.class, pid, callback);
  }

  @Override
  protected void postProcessObjects(List<?> objects) {
    for (Object object : objects) {
      MediaResource picture = ((Store) object).getLogo();
      if (picture != null) {
        picture.setDownloader(context.getResourceDownloader());
        if (!picture.isCached()) {
          picture.download();
        }
      }
    }
  }
}
