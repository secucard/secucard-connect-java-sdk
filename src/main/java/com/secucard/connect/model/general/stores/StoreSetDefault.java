package com.secucard.connect.model.general.stores;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Steffen Schröder on 26.02.15.
 */
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
