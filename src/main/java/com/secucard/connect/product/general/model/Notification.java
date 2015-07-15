package com.secucard.connect.product.general.model;

import com.secucard.connect.product.common.model.SecuObject;

public class Notification extends SecuObject {
  private String text;

  public Notification() {
  }

  public Notification(String text) {
    this.text = text;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return "Notification{" +
        "text='" + text + '\'' +
        "} " + super.toString();
  }
}
