/*
 * Copyright (c) 2014 secucard AG. All rights reserved
 */

package com.secucard.connect.model.general.components;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class DayTime implements Serializable {

    @JsonProperty
    private int day;

    @JsonProperty
    private String time;

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}