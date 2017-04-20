package com.secucard.connect.product.payment.model;

// Subscription Data Model class
public class Subscription {

  public String purpose;

  public String getPurpose() {
    return purpose;
  }

  public void setPurpose(String purpose) {
    this.purpose = purpose;
  }

  @Override
  public String toString() {
    return "Subscription{" + "purpose='" + purpose + '\'' + '}';
  }

}
