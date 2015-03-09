package com.secucard.connect.service.general.apps;

import com.secucard.connect.Callback;
import com.secucard.connect.ClientContext;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.QueryParams;
import com.secucard.connect.model.general.Store;
import com.secucard.connect.model.loyalty.Card;
import com.secucard.connect.model.transport.Result;
import com.secucard.connect.service.AbstractService;

public class SecuAppService extends AbstractService {

  /**
   * Get the merchant with the given ID
   *
   * @param appId     App ID
   * @param argObject Object with Store ID and Merchant ID
   * @return The merchant with the given ID or null if not found
   */
  public ObjectList<Store> getMerchant(String appId, Object argObject, Callback callback) {
    return execute(appId, "getMerchantDetails", argObject, StoreList.class, callback, ClientContext.REST);
  }

  /**
   * Get a list of merchants
   *
   * @param appId App ID
   * @param arg   Arguments to filter list
   * @return List of merchants
   */
  public ObjectList<Store> getMerchants(String appId, QueryParams arg, final Callback callback) {

    return execute(appId, "getMyMerchants", arg, StoreList.class, callback, ClientContext.REST);

  }

  /**
   * Add card to account
   *
   * @param appId App ID
   * @param argObject Arguments with cardnumber and pin
   * @return True if card added successfully, else false
   */
  public Boolean addCard(final String appId, final Object argObject, Callback callback) {
    return new Result2BooleanInvoker() {
      @Override
      protected Result handle(Callback<Result> callback) throws Exception {
        return execute(appId, "addCard", argObject, Result.class, callback, ClientContext.REST);
      }
    }.invokeAndConvert(callback);
  }

  public static class StoreList extends ObjectList<Store> {

  }

}
