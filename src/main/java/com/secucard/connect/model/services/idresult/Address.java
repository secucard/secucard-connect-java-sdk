package com.secucard.connect.model.services.idresult;

public class Address {
  private Value zipcode;
  private Value country;
  private Value city;
  private Value street;

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

  @Override
  public String toString() {
    return "Address{" +
        "zipcode=" + zipcode +
        ", country=" + country +
        ", city=" + city +
        ", street=" + street +
        '}';
  }
}
