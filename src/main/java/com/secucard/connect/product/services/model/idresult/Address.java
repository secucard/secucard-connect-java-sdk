package com.secucard.connect.product.services.model.idresult;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Address {
  @JsonProperty("postal_code")
  private Value postalCode;

  private Value country;

  private Value city;

  private Value street;

  @JsonProperty("street_number")
  private Value streetNumber;

  public Value getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(Value postalCode) {
    this.postalCode = postalCode;
  }

  public Value getCountry() {
    return country;
  }

  public void setCountry(Value country) {
    this.country = country;
  }

  public Value getCity() {
    return city;
  }

  public void setCity(Value city) {
    this.city = city;
  }

  public Value getStreet() {
    return street;
  }

  public void setStreet(Value street) {
    this.street = street;
  }

  public Value getStreetNumber() {
    return streetNumber;
  }

  public void setStreetNumber(Value streetNumber) {
    this.streetNumber = streetNumber;
  }

  @Override
  public String toString() {
    return "Address{" +
        "zipcode=" + postalCode +
        ", country=" + country +
        ", city=" + city +
        ", street=" + street +
        ", streetNumber=" + streetNumber +
        '}';
  }
}
