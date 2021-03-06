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

package com.secucard.connect.product.loyalty;

import com.secucard.connect.client.Callback;
import com.secucard.connect.client.ProductService;
import com.secucard.connect.net.Options;
import com.secucard.connect.product.loyalty.model.Customer;

/**
 * Implements the loyalty/customers operations.
 */
public class CustomersService extends ProductService<Customer> {

  public static final ServiceMetaData<Customer> META_DATA = new ServiceMetaData<>("loyalty", "customers", Customer.class);

  @Override
  public ServiceMetaData<Customer> getMetaData() {
    return META_DATA;
  }

  @Override
  public Customer get(String id) {
    return get(id, null);
  }

  @Override
  public Customer get(String id, Callback<Customer> callback) {
    Options options = Options.getDefault();
    options.resultProcessing = new Callback.Notify<Customer>() {
      @Override
      public void notify(Customer customer) {
        downloadMedia(customer.getPictureObject());
      }
    };
    return super.get(id, options, callback);
  }

}
