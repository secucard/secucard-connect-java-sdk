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

public class NewsService extends ProductService<News> {

  @Override
  public ServiceMetaData<News> createMetaData() {
    return new ServiceMetaData<>("general", "news", News.class);
  }

  /**
   * Mark news as read
   *
   * @param pid News ID
   * @return True if successfully updated, false else.
   */
  public Boolean markRead(final String pid, Callback<Boolean> callback) {
    return executeToBool(pid, "markRead", null, null, callback);
  }

}
