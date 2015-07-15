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
