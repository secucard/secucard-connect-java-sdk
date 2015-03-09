package com.secucard.connect.model.general;

import com.secucard.connect.model.SecuObject;

public class Notification extends SecuObject {
  public static final String OBJECT = "general.notifications";

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
  public String getObject() {
    return OBJECT;
  }


  @Override
  public String toString() {
    return "Notification{" +
        "text='" + text + '\'' +
        "} " + super.toString();
  }
}
