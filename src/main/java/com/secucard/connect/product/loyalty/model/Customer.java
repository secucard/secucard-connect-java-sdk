/*
 * Copyright (c) 2015. hp.weber GmbH & Co secucard KG (www.secucard.com)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.secucard.connect.product.loyalty.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.product.common.model.MediaResource;
import com.secucard.connect.product.common.model.SecuObject;
import com.secucard.connect.product.general.model.Contact;
import com.secucard.connect.product.general.model.Merchant;

import java.util.Date;
import java.util.List;

public class  Customer extends SecuObject {
  private Merchant merchant;

  @JsonProperty("contact")
  private Contact contact;

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

  public Contact getContact() {
    return contact;
  }

  public void setContact(Contact contact) {
    this.contact = contact;
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
