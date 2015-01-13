/*
 * Copyright (c) 2014 secucard AG. All rights reserved
 */

package com.secucard.connect.model.general.accounts.BeaconEnvironment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.model.SecuObject;

import java.util.ArrayList;

public class BeaconList extends SecuObject {

    @JsonProperty
    private ArrayList<BeaconEnvironment> beacons;

    public ArrayList<BeaconEnvironment> getBeacons() {
        return beacons;
    }

    public void setBeacons(ArrayList<BeaconEnvironment> beacons) {
        this.beacons = beacons;
    }
}
