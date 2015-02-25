package com.secucard.connect.model.general.stores;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.annotation.ProductInfo;
import com.secucard.connect.model.general.merchant.Merchant;

/**
 * Created by Steffen Schröder on 25.02.15.
 */
@ProductInfo(resourceId = "general.stores")
public class Store extends SecuObject {

    @JsonProperty
    private String name;

    @JsonProperty("name_raw")
    private String nameRaw;

    @JsonProperty
    private Merchant merchant;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameRaw() {
        return nameRaw;
    }

    public void setNameRaw(String nameRaw) {
        this.nameRaw = nameRaw;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }
}
