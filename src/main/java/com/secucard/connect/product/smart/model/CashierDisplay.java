package com.secucard.connect.product.smart.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CashierDisplay {

  private String type;

  @JsonProperty("device_id")
  private String deviceId;

  private String action;

  private String value;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "CashierDisplay{" +
        "deviceId='" + deviceId + '\'' +
        ", action='" + action + '\'' +
        ", value='" + value + '\'' +
        '}';
  }
}
