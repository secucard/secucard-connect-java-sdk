package com.secucard.connect.model.services.idresult;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ContactData {
  @JsonProperty("mobilephone")
  String mobilePhone;

  String email;

  public String getMobilePhone() {
    return mobilePhone;
  }

  public void setMobilePhone(String mobilePhone) {
    this.mobilePhone = mobilePhone;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Override
  public String toString() {
    return "ContactData{" +
        "mobilePhone='" + mobilePhone + '\'' +
        ", email='" + email + '\'' +
        '}';
  }
}
