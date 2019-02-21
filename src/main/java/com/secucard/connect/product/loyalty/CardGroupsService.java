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
import com.secucard.connect.product.loyalty.model.CardGroup;
import java.util.HashMap;
import java.util.Map;

/**
 * Implements the loyalty/cardgroups operations.
 */
public class CardGroupsService extends ProductService<CardGroup> {

  public static final ServiceMetaData<CardGroup> META_DATA = new ServiceMetaData<>("loyalty", "cardgroups", CardGroup.class);

  @Override
  public ServiceMetaData<CardGroup> getMetaData() {
    return META_DATA;
  }

  /**
   * Check if a passcode is enabled for this card and transaction type
   *
   * @param cardGroupId CRG_XYZ
   * @param transactionType Type of transaction to check {@link CardGroup}
   * @param cardNumber Number of the card
   * @return bool
   */
  public Boolean checkPasscodeEnabled(String cardGroupId, String transactionType, String cardNumber, Callback<Boolean> callback) {
    if (cardGroupId == null || cardGroupId.equals("")) {
      throw new IllegalArgumentException("Parameter [cardGroupId] can not be empty!");
    }

    if (transactionType == null || transactionType.equals("")) {
      throw new IllegalArgumentException("Parameter [transactionType] can not be empty!");
    }

    if (cardNumber == null || cardNumber.equals("")) {
      throw new IllegalArgumentException("Parameter [cardNumber] can not be empty!");
    }

    Map<String, String> obj = new HashMap<>();
    obj.put("action", transactionType);
    obj.put("cardnumber", cardNumber);

    return super.executeToBool(cardGroupId, "CheckPasscodeEnabled", null, obj, null, callback);
  }
}