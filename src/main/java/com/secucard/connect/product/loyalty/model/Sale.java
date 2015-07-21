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
import com.secucard.connect.product.general.model.Store;

import java.io.Serializable;
import java.util.Currency;
import java.util.Date;
import java.util.List;

public class Sale extends SecuObject {
  @JsonProperty
  private int amount;

  @JsonProperty("last_change")
  private Date lastChange;

  @JsonProperty
  private int status;

  @JsonProperty
  private String description;

  @JsonProperty("description_raw")
  private String descriptionRaw;

  @JsonProperty
  private Store store;

  @JsonProperty
  private Card card;

  @JsonProperty
  private CardGroup cardgroup;

  @JsonProperty
  private MerchantCard merchantcard;

  @JsonProperty("balance_amount")
  private int balanceAmount;

  @JsonProperty("balance_points")
  private int balancePoints;

  private Currency currency;

  private List<Bonus> bonus;

  public int getAmount() {
    return amount;
  }

  public void setAmount(int amount) {
    this.amount = amount;
  }

  public Currency getCurrency() {
    return currency;
  }

  public void setCurrency(Currency currency) {
    this.currency = currency;
  }

  public Date getLastChange() {
    return lastChange;
  }

  public void setLastChange(Date lastChange) {
    this.lastChange = lastChange;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDescriptionRaw() {
    return descriptionRaw;
  }

  public void setDescriptionRaw(String descriptionRaw) {
    this.descriptionRaw = descriptionRaw;
  }

  public Store getStore() {
    return store;
  }

  public void setStore(Store store) {
    this.store = store;
  }

  public Card getCard() {
    return card;
  }

  public void setCard(Card card) {
    this.card = card;
  }

  public CardGroup getCardgroup() {
    return cardgroup;
  }

  public void setCardgroup(CardGroup cardgroup) {
    this.cardgroup = cardgroup;
  }

  public MerchantCard getMerchantcard() {
    return merchantcard;
  }

  public void setMerchantcard(MerchantCard merchantcard) {
    this.merchantcard = merchantcard;
  }

  public int getBalanceAmount() {
    return balanceAmount;
  }

  public void setBalanceAmount(int balanceAmount) {
    this.balanceAmount = balanceAmount;
  }

  public int getBalancePoints() {
    return balancePoints;
  }

  public void setBalancePoints(int balancePoints) {
    this.balancePoints = balancePoints;
  }

  public List<Bonus> getBonus() {
    return bonus;
  }

  public void setBonus(List<Bonus> bonus) {
    this.bonus = bonus;
  }


  public static class Bonus implements Serializable {
    private int amount;

    /**
     * PTS or EUR
     */
    private String currency;

    private int balance;

    public int getAmount() {
      return amount;
    }

    public void setAmount(int amount) {
      this.amount = amount;
    }

    public String getCurrency() {
      return currency;
    }

    public void setCurrency(String currency) {
      this.currency = currency;
    }

    public int getBalance() {
      return balance;
    }

    public void setBalance(int balance) {
      this.balance = balance;
    }
  }
}
