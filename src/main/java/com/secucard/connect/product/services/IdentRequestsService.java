package com.secucard.connect.product.services;

import com.secucard.connect.client.ProductService;
import com.secucard.connect.product.services.model.IdentRequest;

public class IdentRequestsService extends ProductService<IdentRequest> {

  @Override
  protected ServiceMetaData<IdentRequest> createMetaData() {
    return new ServiceMetaData<>("services", "identrequests", IdentRequest.class);
  }
}
