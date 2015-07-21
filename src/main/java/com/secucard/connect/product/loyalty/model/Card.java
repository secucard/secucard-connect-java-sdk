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

package com.secucard.connect.product.loyalty.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.product.common.model.SecuObject;
import com.secucard.connect.product.general.model.Account;

public class Card extends SecuObject {
  @JsonProperty("cardnumber")
  private String cardNumber;

  @JsonProperty
  private String created;

  @JsonProperty
  private Account account;

  public String getCardNumber() {
    return cardNumber;
  }

  public void setCardNumber(String cardNumber) {
    this.cardNumber = cardNumber;
  }

  public String getCreated() {
    return created;
  }

  public void setCreated(String created) {
    this.created = created;
  }

  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }
}
