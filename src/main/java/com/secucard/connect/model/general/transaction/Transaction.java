package com.secucard.connect.model.general.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.annotation.ProductInfo;
import com.secucard.connect.model.general.merchant.Merchant;

import java.util.Date;

/**
 * Created by Steffen on 26.08.2014.
 */
@ProductInfo(resourceId = "general.transactions")
public class Transaction extends SecuObject {

    @JsonProperty
    private Merchant merchant;

    @JsonProperty
    private String currency;

    @JsonProperty
    private double amount;

    @JsonProperty("last_change")
    private Date lastChange;

    @JsonProperty
    private String type;

    @JsonProperty
    private Details details;


    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Details getDetails() {
        return details;
    }

    public void setDetails(Details details) {
        this.details = details;
    }
}
