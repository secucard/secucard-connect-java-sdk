package com.secucard.connect.model.general.merchant;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.general.accounts.location.Location;
import com.secucard.connect.model.general.components.MetaData;

/**
 * Created by Steffen Schröder on 26.08.2014.
 * Copyright (c) 2014 secucard AG. All rights reserved.
 */
public class Merchant extends SecuObject {

    @JsonProperty
    private String name;

    @JsonProperty
    private String email;

    @JsonProperty
    private MetaData metadata;

    @JsonProperty
    private Location location;

    public Merchant() {
    }

    public Merchant(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public MetaData getMetadata() {
        return metadata;
    }

    public void setMetadata(MetaData metadata) {
        this.metadata = metadata;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
