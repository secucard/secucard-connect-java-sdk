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
import com.secucard.connect.product.general.model.StoreSetDefault;

import java.util.Arrays;
import java.util.List;

/**
 * Implements the general/stores operations.
 */

public class StoresService extends ProductService<Store> {

  public static final ServiceMetaData<Store> META_DATA = new ServiceMetaData<>("general", "stores", Store.class);

  @Override
  public ServiceMetaData<Store> getMetaData() {
    return META_DATA;
  }

  /**
   * Check in the store with the given id.
   *
   * @return True if successfully updated, false else.
   */
  public boolean checkIn(String storeId, Callback<Boolean> callback) {
//    return super.executeToBool(null, "checkin", storeId, null, callback);
    Options options = getDefaultOptions();
    options.channel = Options.CHANNEL_STOMP;
    return executeToBool(storeId, "checkin", null, null, null, callback);
  }

  /**
   * Check out of the store with the given id.
   *
   * @return True if successfully updated, false else.
   */
  public boolean checkOut(String storeId, Callback<Boolean> callback) {
//    return super.executeToBool(null, "checkin", storeId, null, callback);
    Options options = getDefaultOptions();
    options.channel = Options.CHANNEL_STOMP;
    return executeToBool(storeId, "checkin", "false", null, null, callback);
  }

  /**
   * Set store with given id as default.
   *
   * @return True if successfully updated, false else.
   */
  public boolean setDefault(final String id, StoreSetDefault reason, Callback<Boolean> callback) {
    return super.executeToBool(id, "setDefault", reason, null, callback);
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

  public Store get(String pid, Callback<Store> callback) {
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
