package com.secucard.connect.model.loyalty.merchantcards;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.general.merchant.Merchant;
import com.secucard.connect.model.general.stores.Store;
import com.secucard.connect.model.loyalty.cardgroups.CardGroup;
import com.secucard.connect.model.loyalty.cards.Card;

import java.util.Date;

/**
 * Created by Steffen Schröder on 25.02.15.
 */
public class MerchantCard extends SecuObject {
  public static final String OBJECT = "loyalty.merchantcards";

  @JsonProperty
    private Merchant merchant;

    @JsonProperty("created_for_merchant")
    private Merchant createdForMerchant;

    @JsonProperty
    private Card card;

    @JsonProperty("created_for_store")
    private Store createdForStore;

    @JsonProperty("is_base_card")
    private boolean isBaseCard;

    @JsonProperty
    private CardGroup cardgroup;

    // todo: Customer property ergänzen

    @JsonProperty
    private int balance;

    @JsonProperty
    private int points;

    @JsonProperty("last_usage")
    private Date lastUsage;

    @JsonProperty("last_charge")
    private Date lastCharge;

    @JsonProperty("stock_status")
    private String stockStatus;

    @JsonProperty("lock_status")
    private String lockStatus;

  @Override
  public String getObject() {
    return OBJECT;
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
}
