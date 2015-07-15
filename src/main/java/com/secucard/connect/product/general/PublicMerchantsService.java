package com.secucard.connect.product.general;

import com.secucard.connect.client.ProductService;
import com.secucard.connect.product.general.model.PublicMerchant;

public class PublicMerchantsService extends ProductService<PublicMerchant> {

  @Override
  public ServiceMetaData<PublicMerchant> createMetaData() {
    return new ServiceMetaData<>("general", "publicmerchants", PublicMerchant.class);
  }

}
