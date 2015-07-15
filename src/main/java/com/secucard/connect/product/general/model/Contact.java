package com.secucard.connect.product.general.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.product.common.model.MediaResource;
import com.secucard.connect.product.common.model.SecuObject;
import com.secucard.connect.util.LocaleUtil;

import java.util.Date;
import java.util.Locale;

public class Contact extends SecuObject {
  public static final String OBJECT = "general.contacts";

  public static final String GENDER_MALE = "MALE";
  public static final String GENDER_FEMALE = "FEMALE";

  private String salutation;

  private String title;

  private String name;

  private String forename;

  private String surname;

  private String gender;

  private String nationality;  // ISO 3166 country code like DE

  @JsonIgnore
  private Locale nationalityLocale = null;

  @JsonProperty("dob")
  @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
  private Date dateOfBirth;

  @JsonProperty("birthplace")
  private String birthPlace;

  @JsonProperty("companyname")
  private String companyName;

  private String email;

  private String phone;

  private String mobile;

  private Address address = new Address();

  @JsonProperty("url_website")
  private String urlWebsite;

  private String picture;

  @JsonIgnore
  private MediaResource pictureObject;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public String getBirthPlace() {
    return birthPlace;
  }

  public void setBirthPlace(String birthPlace) {
    this.birthPlace = birthPlace;
  }

  public String getNationality() {
    return nationality;
  }

  /**
   * Returns a locale instance according to the persons nationality.
   */
  public Locale getNationalityLocale() {
    return nationalityLocale;
  }

  /**
   * Setting the nationality in ISO 3166 2 letter code.
   * Case doesn't matter, will be corrected automatically.
   *
   * @param nationality The country code string.
   */
  @JsonProperty
  public void setNationality(String nationality) {
    Locale locale = LocaleUtil.toLocale(nationality, nationalityLocale);
    if (locale == null) {
      this.nationality = nationality;
    } else {
      setNationality(locale);
    }
  }


  /**
   * Set the ISO nationality code by using a locale instance which is less error-prone then using a string
   */
  @JsonIgnore
  public void setNationality(Locale locale) {
    nationalityLocale = locale;
    this.nationality = locale.getCountry();
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

  public String getUrlWebsite() {
    return urlWebsite;
  }

  public void setUrlWebsite(String urlWebsite) {
    this.urlWebsite = urlWebsite;
  }

  public MediaResource getPictureObject() {
    return pictureObject;
  }

  public String getPicture() {
    return picture;
  }

  public void setPicture(String value) {
    this.picture = value;
    pictureObject = MediaResource.create(picture);
  }

  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }




  @Override
  public String toString() {
    return "Contact{" +
        ", foreName='" + forename + '\'' +
        ", companyName='" + companyName + '\'' +
        ", surName='" + surname + '\'' +
        ", title='" + title + '\'' +
        ", salutation='" + salutation + '\'' +
        ", gender='" + gender + '\'' +
        ", email='" + email + '\'' +
        ", dateOfBirth=" + dateOfBirth +
        ", birthPlace='" + birthPlace + '\'' +
        ", phone='" + phone + '\'' +
        ", mobile='" + mobile + '\'' +
        ", nationality='" + nationality + '\'' +
        ", nationalityLocale=" + nationalityLocale +
        ", address=" + address +
        ", urlWebsite='" + urlWebsite + '\'' +
        ", picture='" + picture + '\'' +
        "} " + super.toString();
  }
}
