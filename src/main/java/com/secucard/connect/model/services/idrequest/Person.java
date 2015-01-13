package com.secucard.connect.model.services.idrequest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.Locale;

public class Person {
  public static final String GENDER_MALE = "MALE";
  public static final String GENDER_FEMALE = "FEMALE";

  private String transaction_id;

  private String redirect_url;

  private String status;

  @JsonProperty("owner_transaction_id")
  private String ownerTransactionId;

  private String firstname;
  private String lastname;
  private Date birthdate;
  private String birthplace;
  private String gender;
  private String email;
  private String nationality;  // ISO 3166 country code like DE
  private String mobilephone;
  private String street;
  private String city;
  private String zipcode;
  private String country;  // ISO 3166 country code like DE

  private String custom1;
  private String custom2;
  private String custom3;
  private String custom4;
  private String custom5;

  @JsonIgnore
  private Locale nationalityLocale = null;

  @JsonIgnore
  private Locale countryLocale = null;

  public String getTransaction_id() {
    return transaction_id;
  }

  public void setTransaction_id(String transaction_id) {
    this.transaction_id = transaction_id;
  }

  public String getRedirect_url() {
    return redirect_url;
  }

  public void setRedirect_url(String redirect_url) {
    this.redirect_url = redirect_url;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getOwnerTransactionId() {
    return ownerTransactionId;
  }

  public void setOwnerTransactionId(String ownerTransactionId) {
    this.ownerTransactionId = ownerTransactionId;
  }

  public String getFirstname() {
    return firstname;
  }

  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }

  public String getLastname() {
    return lastname;
  }

  public void setLastname(String lastname) {
    this.lastname = lastname;
  }

  public Date getBirthdate() {
    return birthdate;
  }

  public void setBirthdate(Date birthdate) {
    this.birthdate = birthdate;
  }

  public String getBirthplace() {
    return birthplace;
  }

  public void setBirthplace(String birthplace) {
    this.birthplace = birthplace;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
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
  @JsonIgnore
  public void setNationality(String nationality) {
    Locale locale = toLocale(nationality, nationalityLocale);
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

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getMobilephone() {
    return mobilephone;
  }

  public void setMobilephone(String mobilephone) {
    this.mobilephone = mobilephone;
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getZipcode() {
    return zipcode;
  }

  public void setZipcode(String zipcode) {
    this.zipcode = zipcode;
  }

  public String getCountry() {
    return country;
  }

  @JsonIgnore
  public Locale getCountryLocale() {
    return countryLocale;
  }

  /**
   * Setting the country in ISO 3166 2 letter code.
   * Case doesn't matter, will be corrected automatically.
   *
   * @param country The country code string.
   */
  public void setCountry(String country) {
    Locale locale = toLocale(country, countryLocale);
    if (locale == null) {
      this.country = country;
    } else {
      setCountry(locale);
    }
  }


  /**
   * Set the ISO country code by using a locale instance which is less error-prone then using a string
   */
  @JsonIgnore
  public void setCountry(Locale locale) {
    countryLocale = locale;
    country = locale.getCountry();
  }

  public String getCustom1() {
    return custom1;
  }

  public void setCustom1(String custom1) {
    this.custom1 = custom1;
  }

  public String getCustom2() {
    return custom2;
  }

  public void setCustom2(String custom2) {
    this.custom2 = custom2;
  }

  public String getCustom3() {
    return custom3;
  }

  public void setCustom3(String custom3) {
    this.custom3 = custom3;
  }

  public String getCustom4() {
    return custom4;
  }

  public void setCustom4(String custom4) {
    this.custom4 = custom4;
  }

  public String getCustom5() {
    return custom5;
  }

  public void setCustom5(String custom5) {
    this.custom5 = custom5;
  }

  private static Locale toLocale(String country, Locale locale) {
    if (locale == null || !locale.getCountry().equals(country)) {
      Locale[] locales = Locale.getAvailableLocales();
      for (Locale loc : locales) {
        if (loc.getCountry().equalsIgnoreCase(country)) {
          return loc;
        }
      }
    } else if (locale.getCountry().equals(country)) {
      return locale;
    }
    return null;
  }

  @Override
  public String toString() {
    return "Person{" +
        "transaction_id='" + transaction_id + '\'' +
        ", redirect_url='" + redirect_url + '\'' +
        ", status='" + status + '\'' +
        ", ownerTransactionId='" + ownerTransactionId + '\'' +
        ", firstname='" + firstname + '\'' +
        ", lastname='" + lastname + '\'' +
        ", birthdate=" + birthdate +
        ", birthplace='" + birthplace + '\'' +
        ", gender='" + gender + '\'' +
        ", nationality='" + nationality + '\'' +
        ", email='" + email + '\'' +
        ", mobilephone='" + mobilephone + '\'' +
        ", street='" + street + '\'' +
        ", city='" + city + '\'' +
        ", zipcode='" + zipcode + '\'' +
        ", country='" + country + '\'' +
        ", custom1='" + custom1 + '\'' +
        ", custom2='" + custom2 + '\'' +
        ", custom3='" + custom3 + '\'' +
        ", custom4='" + custom4 + '\'' +
        ", custom5='" + custom5 + '\'' +
        '}';
  }
}
