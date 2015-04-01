package com.secucard.connect.model.services.idresult;

public class ContactData {
  String mobile;

  String email;

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
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
        "mobilePhone='" + mobile + '\'' +
        ", email='" + email + '\'' +
        '}';
  }
}
