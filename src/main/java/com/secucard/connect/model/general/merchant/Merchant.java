package com.secucard.connect.model.general.merchant;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Steffen Schröder on 26.08.2014.
 * Copyright (c) 2014 secucard AG. All rights reserved.
 */
public class Merchant {

    @JsonProperty
    private String id;

    @JsonProperty
    private String a;

    @JsonProperty
    private String b;

    @JsonProperty
    private String name;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
