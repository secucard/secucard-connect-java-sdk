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
