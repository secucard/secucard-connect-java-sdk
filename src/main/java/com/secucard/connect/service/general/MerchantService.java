package com.secucard.connect.service.general;

import com.secucard.connect.Callback;
import com.secucard.connect.ClientContext;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.QueryParams;
import com.secucard.connect.model.general.Merchant;
import com.secucard.connect.service.AbstractService;

import org.json.JSONObject;

public class MerchantService extends AbstractService {

  /**
   * Get the merchant with the given ID
   *
   * @param appId App ID
   * @param arg Arguments with merchant or store ID
   * @return The merchant with the given ID or null if not found
   */
  public Merchant getMerchant(String appId, Object arg, Callback<Merchant> callback) {
    return execute(appId, "getMerchantDetails", arg, Merchant.class, callback, ClientContext.REST);
  }

  /**
   * Get a list of merchants
   *
   * @param appId App ID
   * @param arg Arguments to filter list
   * @return List of merchants
   */
  public ObjectList<Merchant> getMerchants(String appId, QueryParams arg, final Callback callback) {

    return execute(appId, "getMyMerchants", arg, MerchantList.class, callback, ClientContext.REST);

  }

  public static class MerchantList extends ObjectList<Merchant> {

  }

}
