package com.secucard.connect.product.smart.model;

import com.secucard.connect.product.common.model.SecuObject;
import com.secucard.connect.product.general.model.Merchant;

public class Device extends SecuObject {

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
