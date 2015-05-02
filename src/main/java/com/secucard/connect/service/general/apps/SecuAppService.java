package com.secucard.connect.service.general.apps;

import com.secucard.connect.Callback;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.QueryParams;
import com.secucard.connect.model.general.Store;
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
  public StoreList getMerchant(String appId, Object argObject, Callback<StoreList> callback) {
    return new ServiceTemplate().execute(appId, "getMerchantDetails", argObject, StoreList.class, callback);
  }

  /**
   * Get a list of merchants
   *
   * @param appId App ID
   * @param arg   Arguments to filter list
   * @return List of merchants
   */
  public StoreList getMerchants(String appId, QueryParams arg, final Callback<StoreList> callback) {
    return new ServiceTemplate().execute(appId, "getMyMerchants", arg, StoreList.class, callback);
  }

  /**
   * Add card to account
   *
   * @param appId     App ID
   * @param argObject Arguments with card number and pin
   * @return True if card added successfully, else false
   */
  public Boolean addCard(final String appId, final Object argObject, Callback<Boolean> callback) {
    return new ServiceTemplate().executeToBoolean(appId, "addCard", argObject, Result.class, callback);
  }

  /**
   * Just needed to allow parametrized type as result type in execute(...).
   * todo: refactor the type arg
   */
  public static class StoreList extends ObjectList<Store> {

  }

}
