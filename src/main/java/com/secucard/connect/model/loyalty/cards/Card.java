/*
 * Copyright (c) 2014 secucard AG. All rights reserved
 */

package com.secucard.connect.model.loyalty.cards;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.annotation.ProductInfo;
import com.secucard.connect.model.general.accounts.Account;

import java.io.Serializable;

@ProductInfo(resourceId = "loyalty.cards")
public class Card extends SecuObject implements Serializable {

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
