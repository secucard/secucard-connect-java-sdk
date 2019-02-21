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
import com.secucard.connect.product.general.model.Merchant;
import com.secucard.connect.product.general.model.Store;
import java.util.Date;

public class MerchantCard extends SecuObject {

  public static final int PASSCODE_STATUS_NOT_ENABLED = 1;
  public static final int PASSCODE_STATUS_NOT_SET = 2;
  public static final int PASSCODE_STATUS_SET = 3;

  private Merchant merchant;

  @JsonProperty("created_for_merchant")
  private Merchant createdForMerchant;

  private Card card;

  @JsonProperty("created_for_store")
  private Store createdForStore;

  @JsonProperty("is_base_card")
  private boolean isBaseCard;

  private CardGroup cardgroup;

  private Customer customer;

  private int balance;

  @JsonProperty("cash_balance")
  private int cashBalance;

  private int points;

  @JsonProperty("last_usage")
  private Date lastUsage;

  @JsonProperty("last_charge")
  private Date lastCharge;

  @JsonProperty("stock_status")
  private String stockStatus;

  @JsonProperty("lock_status")
  private String lockStatus;

  @JsonProperty("bonus_balance")
  private int bonusBalance;

  @JsonProperty("passcode")
  private int passcode;

  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  public Merchant getMerchant() {
    return merchant;
  }

  public void setMerchant(Merchant merchant) {
    this.merchant = merchant;
  }

  public Merchant getCreatedForMerchant() {
    return createdForMerchant;
  }

  public void setCreatedForMerchant(Merchant createdForMerchant) {
    this.createdForMerchant = createdForMerchant;
  }

  public Card getCard() {
    return card;
  }

  public void setCard(Card card) {
    this.card = card;
  }

  public Store getCreatedForStore() {
    return createdForStore;
  }

  public void setCreatedForStore(Store createdForStore) {
    this.createdForStore = createdForStore;
  }

  public boolean isBaseCard() {
    return isBaseCard;
  }

  public void setBaseCard(boolean isBaseCard) {
    this.isBaseCard = isBaseCard;
  }

  public CardGroup getCardgroup() {
    return cardgroup;
  }

  public void setCardgroup(CardGroup cardgroup) {
    this.cardgroup = cardgroup;
  }

  public int getBalance() {
    return balance;
  }

  public void setBalance(int balance) {
    this.balance = balance;
  }

  public int getCashBalance() {
    return cashBalance;
  }

  public void setCashBalance(int cashBalance) {
    this.cashBalance = cashBalance;
  }

  public int getBonusBalance() {
    return bonusBalance;
  }

  public void setBonusBalance(int bonusBalance) {
    this.bonusBalance = bonusBalance;
  }

  public int getPoints() {
    return points;
  }

  public void setPoints(int points) {
    this.points = points;
  }

  public Date getLastUsage() {
    return lastUsage;
  }

  public void setLastUsage(Date lastUsage) {
    this.lastUsage = lastUsage;
  }

  public Date getLastCharge() {
    return lastCharge;
  }

  public void setLastCharge(Date lastCharge) {
    this.lastCharge = lastCharge;
  }

  public String getStockStatus() {
    return stockStatus;
  }

  public void setStockStatus(String stockStatus) {
    this.stockStatus = stockStatus;
  }

  public String getLockStatus() {
    return lockStatus;
  }

  public void setLockStatus(String lockStatus) {
    this.lockStatus = lockStatus;
  }

  public int getPasscode() {
    return passcode;
  }

  public void setPasscode(int passcode) {
    this.passcode = passcode;
  }
}
