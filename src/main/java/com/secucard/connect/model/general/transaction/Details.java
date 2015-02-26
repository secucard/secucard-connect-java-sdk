package com.secucard.connect.model.general.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.general.stores.Store;
import com.secucard.connect.model.loyalty.cardgroups.CardGroup;
import com.secucard.connect.model.loyalty.cards.Card;
import com.secucard.connect.model.loyalty.merchantcards.MerchantCard;

import java.util.Date;

/**
 * Created by Steffen Schröder on 25.02.15.
 */
public class Details extends SecuObject {

    @JsonProperty
    private String currency;

    @JsonProperty
    private double amount;

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
    private double balanceAmount;

    @JsonProperty("balance_points")
    private int balancePoints;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
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

    public double getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(double balanceAmount) {
        this.balanceAmount = balanceAmount;
    }

    public int getBalancePoints() {
        return balancePoints;
    }

    public void setBalancePoints(int balancePoints) {
        this.balancePoints = balancePoints;
    }
}
