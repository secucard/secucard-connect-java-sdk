package com.secucard.connect.model.services.idresult;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Address {
  private Value zipcode;

  private Value country;

  private Value city;

  private Value street;

  @JsonProperty("streetnumber")
  private Value streetNumber;

  public Value getZipcode() {
    return zipcode;
  }

  public void setZipcode(Value zipcode) {
    this.zipcode = zipcode;
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
        "zipcode=" + zipcode +
        ", country=" + country +
        ", city=" + city +
        ", street=" + street +
        ", streetNumber=" + streetNumber +
        '}';
  }
}
