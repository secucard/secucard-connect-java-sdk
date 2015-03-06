package com.secucard.connect.model.general;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.model.MediaResource;
import com.secucard.connect.model.SecuObject;

import java.net.MalformedURLException;
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
  @JsonIgnore
  private Date dateOfBirth;

  @JsonProperty
  private String phone;

  @JsonProperty
  private String mobile;

  @JsonProperty
  private Address address;

  @JsonProperty("url_website")
  private String websiteUrl;


  @JsonProperty("picture")
  private String pictureUrl;

  @JsonIgnore
  private MediaResource picture;


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

  public MediaResource getPicture() {
    return picture;
  }

  public String getPictureUrl() {
    return pictureUrl;
  }

  public void setPictureUrl(String value) {
    this.pictureUrl = value;
    if (value != null) {
      try {
        this.picture = new MediaResource(value);
      } catch (MalformedURLException e) {
        // ignore here, value could be just an id as well
      }
    }
  }


  @Override
  public String getObject() {
    return OBJECT;
  }
}
