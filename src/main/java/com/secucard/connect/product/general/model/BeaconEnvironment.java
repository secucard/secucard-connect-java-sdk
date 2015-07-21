/*
 * Copyright (c) 2015. hp.weber GmbH & Co secucard KG (www.secucard.com)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.secucard.connect.product.general.model;

import com.fasterxml.jackson.annotation.JsonProperty;

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
