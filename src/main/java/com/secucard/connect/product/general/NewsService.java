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
import com.secucard.connect.product.general.model.News;

/**
 * Implements the general/news operations.
 */

public class NewsService extends ProductService<News> {

  public static final ServiceMetaData<News> META_DATA = new ServiceMetaData<>("general", "news", News.class);

  @Override
  public ServiceMetaData<News> getMetaData() {
    return META_DATA;
  }

  /**
   * Mark news with given id as read.
   *
   * @return True if successfully updated, false else.
   */
  public Boolean markRead(final String id, Callback<Boolean> callback) {
    return executeToBool(id, "markRead", null, null, callback);
  }

}
