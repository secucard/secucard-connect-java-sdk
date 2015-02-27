package com.secucard.connect.model.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.general.merchant.Merchant;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Customer extends SecuObject {
  public static final String OBJECT = "payment.customers";

  private Merchant merchant;

  @JsonProperty("forename")
  private String foreName;

  @JsonProperty("surname")
  private String surName;

  @JsonProperty("companyname")
  private String companyName;

  private String salutation;

  private String title;

  private String street;

  private String zipcode;

  private String city;

  private String email;

  private String phone;

  @JsonProperty("dob")
  private Date dateOfBirth;

  private Date created;

  private Date updated;

  @Override
  public String getObject() {
    return OBJECT;
  }

  public Merchant getMerchant() {
    return merchant;
  }

  public void setMerchant(Merchant merchant) {
    this.merchant = merchant;
  }

  public String getForeName() {
    return foreName;
  }

  public void setForeName(String foreName) {
    this.foreName = foreName;
  }

  public String getSurName() {
    return surName;
  }

  public void setSurName(String surName) {
    this.surName = surName;
  }

  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public String getSalutation() {
    return salutation;
  }

  public void setSalutation(String salutation) {
    this.salutation = salutation;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public String getZipcode() {
    return zipcode;
  }

  public void setZipcode(String zipcode) {
    this.zipcode = zipcode;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getDateOfBirth() {
    return dateOfBirth == null ? null : new SimpleDateFormat("YYYY-MM-dd").format(dateOfBirth);
  }

  public Date getDateOfBirthAsDate() {
    return dateOfBirth;
  }

  public void setDateOfBirth(Date dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public Date getUpdated() {
    return updated;
  }

  public void setUpdated(Date updated) {
    this.updated = updated;
  }

  @Override
  public String toString() {
    return "Customer{" +
        "merchant=" + merchant +
        ", foreName='" + foreName + '\'' +
        ", sureName='" + surName + '\'' +
        ", companyName='" + companyName + '\'' +
        ", salutation='" + salutation + '\'' +
        ", title='" + title + '\'' +
        ", street='" + street + '\'' +
        ", zipcode='" + zipcode + '\'' +
        ", city='" + city + '\'' +
        ", email='" + email + '\'' +
        ", phone='" + phone + '\'' +
        ", dateOfBirth=" + dateOfBirth +
        ", created=" + created +
        ", updated=" + updated +
        '}';
  }
}
