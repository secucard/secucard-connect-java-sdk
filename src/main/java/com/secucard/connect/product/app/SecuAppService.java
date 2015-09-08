/*
 * Copyright (c) 2015. hp.weber GmbH & Co secucard KG (www.secucard.com)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.secucard.connect.product.app;

import com.secucard.connect.client.Callback;
import com.secucard.connect.client.ProductService;
import com.secucard.connect.product.common.model.ObjectList;
import com.secucard.connect.product.common.model.QueryParams;
import com.secucard.connect.product.common.model.SecuObject;
import com.secucard.connect.product.general.model.Store;

import java.io.Serializable;

/**
 * Implements the operation for the secucard android app.
 */
public class SecuAppService extends ProductService<SecuObject> {
  @Override
  public ServiceMetaData<SecuObject> getMetaData() {
    return new ServiceMetaData<>(context.appId); // get configured app id
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

