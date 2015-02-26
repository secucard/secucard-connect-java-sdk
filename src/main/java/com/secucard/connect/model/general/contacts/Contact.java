package com.secucard.connect.model.general.contacts;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.model.SecuObject;

/**
 * Created by Steffen Schröder on 23.02.15.
 */
public class Contact extends SecuObject {
  public static final String OBJECT = "general.contacts";

  @JsonProperty
  private String name;

  @JsonProperty
  private String forename;

  @JsonProperty
  private String surname;

  @JsonProperty
  private String salutation;

  @JsonProperty
  private String email;

  @JsonProperty("dob")
  private String birthday;

  @JsonProperty("url_website")
  private String websiteUrl;

  @JsonProperty
  private String phone;

  @JsonProperty
  private String mobile;

  @JsonProperty
  private Address address;


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getForename() {
    return forename;
  }

  public void setForename(String forename) {
    this.forename = forename;
  }

  public String getSurname() {
    return surname;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  public String getSalutation() {
    return salutation;
  }

  public void setSalutation(String salutation) {
    this.salutation = salutation;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public String getBirthday() {
    return birthday;
  }

  public void setBirthday(String birthday) {
    this.birthday = birthday;
  }

  public String getWebsiteUrl() {
    return websiteUrl;
  }

  public void setWebsiteUrl(String websiteUrl) {
    this.websiteUrl = websiteUrl;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  @Override
  public String getObject() {
    return OBJECT;
  }
}
