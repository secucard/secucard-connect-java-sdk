package com.secucard.connect.product.general.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StoreSetDefault {

    @JsonProperty
    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
