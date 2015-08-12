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

package com.secucard.connect.product.payment;

import com.secucard.connect.client.Callback;
import com.secucard.connect.client.ProductService;
import com.secucard.connect.product.payment.model.Container;

/**
 * Implements the payment/containers operations.
 */
public class ContainersService extends ProductService<Container> {

  public static final ServiceMetaData<Container> META_DATA = new ServiceMetaData<>("payment", "containers",
      Container.class);

  @Override
  public ServiceMetaData<Container> getMetaData() {
    return META_DATA;
  }

  /**
   * Creating a payment container.
   * The container data must at least provide a valid IBAN in private data!
   * See {@link com.secucard.connect.product.payment.model.Data}, all other data is optional.
   * Returns validated and completed container data on success.
   * Throws exception when validation failed, see "throws" section.
   *
   * @param container The payment container data.
   * @param callback  Callback to get notified when completed or failed.
   * @return The validated and completed data like Container.publicData.
   * @throws com.secucard.connect.client.APIError if a error happens. userMessage field has the reason,
   *                                                              like "invalid iban and bic combination".
   */
  public Container create(final Container container, Callback<Container> callback) {
    return super.create(container, callback);
  }

  /**
   * Assigns a customer with given id to a container and return the updated container.
   */
  public Container assignCustomer(final String containerId, final String customerId, Callback<Container> callback) {
    return super.execute(containerId, "assign", customerId, null, Container.class, null, callback);
  }

  /**
   * Removes an assigned customer from a container with given id.
   */
  public void removeCustomer(final String containerId, Callback<Void> callback) {
    super.delete(containerId, "assign", null, null, callback);
  }
}
