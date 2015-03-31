package com.secucard.connect.model.loyalty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.model.MediaResource;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.general.Merchant;

import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;

public class  Customer extends SecuObject {
  public static final String OBJECT = "loyalty.customers";

  private Merchant merchant;

  @JsonProperty("forename")
  private String foreName;

  @JsonProperty("surname")
  private String surName;

  private String company;

  @JsonProperty("display_name")
  private String displayName;

  private String salutation;

  private String title;

  private String street;

  private String zipcode;

  private String city;

  private String email;

  private String fax;

  private String mobile;

  private String note;

  private String phone;

  private String age;

  @JsonProperty("days_until_birthday")
  private String daysUntilBirthday;

  @JsonProperty("additional_data")
  private List<String> additionalData;

  @JsonProperty("customernumber")
  private String customerNumber;

  @JsonProperty("dob")
  private Date dateOfBirth;

  private String picture;

  @JsonIgnore
  private MediaResource pictureObject;

  @Override
  public String getObject() {
    return OBJECT;
  }

  public String getPicture() {
    return picture;
  }

  public void setPicture(String value) {
    picture = value;
    pictureObject = MediaResource.create(picture);
  }

  public MediaResource getPictureObject() {
    return pictureObject;
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

  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
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

  public String getFax() {
    return fax;
  }

  public void setFax(String fax) {
    this.fax = fax;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getAge() {
    return age;
  }

  public void setAge(String age) {
    this.age = age;
  }

  public String getDaysUntilBirthday() {
    return daysUntilBirthday;
  }

  public void setDaysUntilBirthday(String daysUntilBirthday) {
    this.daysUntilBirthday = daysUntilBirthday;
  }

  public List<String> getAdditionalData() {
    return additionalData;
  }

  public void setAdditionalData(List<String> additionalData) {
    this.additionalData = additionalData;
  }

  public String getCustomerNumber() {
    return customerNumber;
  }

  public void setCustomerNumber(String customerNumber) {
    this.customerNumber = customerNumber;
  }

  public Date getDateOfBirth() {
    return dateOfBirth;
  }

  public void setDateOfBirth(Date dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
  }
}
