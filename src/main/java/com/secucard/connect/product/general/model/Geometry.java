/*
 * Copyright (c) 2014 secucard AG. All rights reserved
 */

package com.secucard.connect.product.general.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Geometry implements Serializable {

  @JsonProperty
  private double lat;

  @JsonProperty
  private double lon;

  public Geometry() {
  }

  public Geometry(double lat, double lon) {
    this.lat = lat;
    this.lon = lon;
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
}
