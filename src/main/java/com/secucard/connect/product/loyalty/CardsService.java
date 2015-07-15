package com.secucard.connect.product.loyalty;

import com.secucard.connect.client.Callback;
import com.secucard.connect.client.ProductService;
import com.secucard.connect.product.loyalty.model.Card;

public class CardsService extends ProductService<Card> {

  @Override
  public ServiceMetaData<Card> createMetaData() {
    return new ServiceMetaData<>("loyalty", "cards", Card.class);
  }

  /**
   * Assign a card
   *
   * @param cardNumber Card number
   * @return Assigned card
   */
  public Boolean assignUser(final String cardNumber, final Object pin, Callback<Boolean> callback) {
    return super.executeToBool(cardNumber, "assignUser", "me", pin, null, callback);
  }

  public void deleteUserFromCard(final String cardNumber, Callback<Void> callback) {
    super.delete(cardNumber, "assignUser", "me", null, callback);
  }

}
