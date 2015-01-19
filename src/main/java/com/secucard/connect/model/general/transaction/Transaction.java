package com.secucard.connect.model.general.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.model.general.merchant.Merchant;

/**
 * Created by Steffen on 26.08.2014.
 */
public class Transaction {

    @JsonProperty
    private String id;

    @JsonProperty
    private String a;

    @JsonProperty
    private String b;

    @JsonProperty
    private Merchant merchant;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }
}
