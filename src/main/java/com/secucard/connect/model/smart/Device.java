package com.secucard.connect.model.smart;

import com.secucard.connect.model.SecuObject;

public class Device extends SecuObject {
  public static final String OBJECT = "smart.devices";

  private String type;

  public Device() {
  }

  public Device(String id) {
    this.id = id;
  }

  public Device(String id, String type) {
    this.id = id;
    this.type = type;
  }

  @Override
  public String getObject() {
    return OBJECT;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return "Device{" +
        "type='" + type + '\'' +
        '}';
  }
}
