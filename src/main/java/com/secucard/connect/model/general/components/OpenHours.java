/*
 * Copyright (c) 2014 secucard AG. All rights reserved
 */

package com.secucard.connect.model.general.components;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class OpenHours implements Serializable {

    @JsonProperty
    private DayTime open;

    @JsonProperty
    private DayTime close;

    public DayTime getOpen() {
        return open;
    }

    public void setOpen(DayTime open) {
        this.open = open;
    }

    public DayTime getClose() {
        return close;
    }

    public void setClose(DayTime close) {
        this.close = close;
    }

}
