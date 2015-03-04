package com.secucard.connect.model.smart;

import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.general.Merchant;

public class Device extends SecuObject {
  public static final String OBJECT = "smart.devices";

  private String type;

  private Merchant merchant;

  public Device() {
  }

  public Merchant getMerchant() {
    return merchant;
  }

  public void setMerchant(Merchant merchant) {
    this.merchant = merchant;
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
