package com.secucard.connect.product.loyalty;

import com.secucard.connect.client.ProductService;
import com.secucard.connect.product.loyalty.model.MerchantCard;

public class MerchantCardsService extends ProductService<MerchantCard> {

  @Override
  public ServiceMetaData<MerchantCard> createMetaData() {
    return new ServiceMetaData<>("loyalty", "merchantcards", MerchantCard.class);
  }

}
