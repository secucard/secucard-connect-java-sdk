/*
 * Copyright (c) 2014 secucard AG. All rights reserved
 */

package com.secucard.connect.model.general.accounts.BeaconEnvironment;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Steffen Schröder on 22.09.2014.
 * Copyright (c) 2014 secucard AG. All rights reserved.
 */
public class BeaconEnvironment {

    @JsonProperty
    private String name;

    @JsonProperty
    private String proximityUUID;

    @JsonProperty
    private String macAddress;

    @JsonProperty
    private int major;

    @JsonProperty
    private int minor;

    @JsonProperty
    private int measuredPower;

    @JsonProperty
    private int rssi;

    @JsonProperty
    private double accuracy;

    @JsonProperty
    private String proximity;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProximityUUID() {
        return proximityUUID;
    }

    public void setProximityUUID(String proximityUUID) {
        this.proximityUUID = proximityUUID;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public int getMeasuredPower() {
        return measuredPower;
    }

    public void setMeasuredPower(int measuredPower) {
        this.measuredPower = measuredPower;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public String getProximity() {
        return proximity;
    }

    public void setProximity(String proximity) {
        this.proximity = proximity;
    }
}