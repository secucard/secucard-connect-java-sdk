package com.secucard.connect.product.general;

import com.secucard.connect.client.ProductService;
import com.secucard.connect.product.general.model.Merchant;

public class MerchantsService extends ProductService<Merchant> {

  @Override
  public ServiceMetaData<Merchant> createMetaData() {
      return new ServiceMetaData<>("general", "merchants", Merchant.class);
  }
}
