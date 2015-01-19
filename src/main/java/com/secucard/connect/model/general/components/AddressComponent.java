/*
 * Copyright (c) 2014 secucard AG. All rights reserved
 */

package com.secucard.connect.model.general.components;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;

public class AddressComponent implements Serializable {

    @JsonProperty("long_name")
    private String longName;

    @JsonProperty("short_name")
    private String shortName;

    @JsonProperty
    private ArrayList<String> types;
}
