/*
 * Copyright (c) 2014 secucard AG. All rights reserved
 */

package com.secucard.connect.model.general.accounts.Location;

/**
 * Created by Steffen Schröder on 22.09.2014.
 * Copyright (c) 2014 secucard AG. All rights reserved.
 *
 * Model class to hold latitude and longitude of a location
 */
public class Location {
    private double lat;

    private double lon;

    private float accuracy;

    public Location() {
    }

    public Location(double lat, double lon, float accuracy) {
      this.lat = lat;
      this.lon = lon;
      this.accuracy = accuracy;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }
}
