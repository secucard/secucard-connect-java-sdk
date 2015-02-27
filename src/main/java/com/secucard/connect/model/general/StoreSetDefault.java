package com.secucard.connect.model.general;

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
