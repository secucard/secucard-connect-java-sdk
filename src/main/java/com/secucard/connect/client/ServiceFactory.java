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

package com.secucard.connect.client;

import com.secucard.connect.product.common.model.SecuObject;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * Factory to create product services.
 * Injects all needed resources into a service without the need of having  public setters in service.
 */
public class ServiceFactory {
  public static Map<Class<? extends ProductService>, ProductService<? extends SecuObject>> createServices(
      ClientContext context) {

    Map<Class<? extends ProductService>, ProductService<? extends SecuObject>> serviceMap = new HashMap<>();
    ServiceLoader<ProductService> loader = ServiceLoader.load(ProductService.class,
        Thread.currentThread().getContextClassLoader());

    for (ProductService service : loader) {
      service.setContext(context);
      // Android ServiceLoader impl. doesn't cache services, so keep in a map
      serviceMap.put(service.getClass(), service);
    }

    return serviceMap;
  }
}
