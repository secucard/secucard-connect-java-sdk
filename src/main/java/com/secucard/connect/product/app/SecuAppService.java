package com.secucard.connect.product.app;

import com.secucard.connect.client.Callback;
import com.secucard.connect.client.ProductService;
import com.secucard.connect.product.common.model.ObjectList;
import com.secucard.connect.product.common.model.QueryParams;
import com.secucard.connect.product.common.model.SecuObject;
import com.secucard.connect.product.general.model.Store;

public class SecuAppService extends ProductService<SecuObject> {
  @Override
  public ServiceMetaData<SecuObject> createMetaData() {
    return new ServiceMetaData<>("APP-ID");
  }

  /**
   * Get the merchant with the given ID
   *
   * @param argObject Object with Store ID and Merchant ID
   * @return The merchant with the given ID or null if not found
   */
  public StoreList getMerchant(Object argObject, Callback<StoreList> callback) {
    return execute("getMerchantDetails", argObject, StoreList.class, null, callback);
  }

  /**
   * Get a list of merchants
   *
   * @param arg   Arguments to filter list
   * @return List of merchants
   */
  public StoreList getMerchants(QueryParams arg, final Callback<StoreList> callback) {
    return execute("getMyMerchants", arg, StoreList.class, null, callback);
  }

  /**
   * Add card to account
   *
   * @param argObject Arguments with card number and pin
   * @return True if card added successfully, else false
   */
  public Boolean addCard(final Object argObject, Callback<Boolean> callback) {
    return executeToBool("addCard", argObject, null, callback);
  }

  public static class StoreList extends ObjectList<Store> {

  }
}

