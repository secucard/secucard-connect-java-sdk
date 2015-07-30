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
import com.secucard.connect.product.loyalty.model.Card;

/**
 * Implements the loyalty/cards operations.
 */
public class CardsService extends ProductService<Card> {

  @Override
  protected ServiceMetaData<Card> createMetaData() {
    return new ServiceMetaData<>("loyalty", "cards", Card.class);
  }

  /**
   * Assign current user to a card with given card number and pin.
   *
   * @return True of ok false else.
   */
  public Boolean assignUser(String cardNumber, String pin, Callback<Boolean> callback) {
    return super.executeToBool(cardNumber, "assignUser", "me", pin, null, callback);
  }

  /**
   * Remove the assigned current user from the card with the given number.
   */
  public void removeUser(final String cardNumber, Callback<Void> callback) {
    super.delete(cardNumber, "assignUser", "me", null, callback);
  }

}
