package com.secucard.connect.product.general.model;

import java.io.Serializable;

public class Location implements Serializable {
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
