package com.secucard.connect.model.general;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.model.SecuObject;

import java.util.Date;

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
  private Date dateOfBirth;

  @JsonProperty
  private String phone;

  @JsonProperty
  private String mobile;

  @JsonProperty
  private Address address;

  @JsonProperty("url_website")
  private String websiteUrl;


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

  public Date getDateOfBirth() {
    return dateOfBirth;
  }

  public void setDateOfBirth(Date dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
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

  public String getWebsiteUrl() {
    return websiteUrl;
  }

  public void setWebsiteUrl(String websiteUrl) {
    this.websiteUrl = websiteUrl;
  }

  @Override
  public String getObject() {
    return OBJECT;
  }
}
