package com.secucard.connect.model.general;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.util.LocaleUtil;

import java.io.Serializable;
import java.util.Locale;

public class Address implements Serializable {

  private String street;

  @JsonProperty("street_number")
  private String streetNumber;

  @JsonProperty("postal_code")
  private String postalCode;

  private String city;

  private String country; // ISO 3166 country code like DE

  @JsonIgnore
  private Locale countryLocale = null;

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public String getStreetNumber() {
    return streetNumber;
  }

  public void setStreetNumber(String streetNumber) {
    this.streetNumber = streetNumber;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
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
  @JsonProperty
  public void setCountry(String country) {
    Locale locale = LocaleUtil.toLocale(country, countryLocale);
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


  @Override
  public String toString() {
    return "Address{" +
        "street='" + street + '\'' +
        ", streetNumber='" + streetNumber + '\'' +
        ", postalCode='" + postalCode + '\'' +
        ", city='" + city + '\'' +
        ", country='" + country + '\'' +
        ", countryLocale=" + countryLocale +
        '}';
  }
}
