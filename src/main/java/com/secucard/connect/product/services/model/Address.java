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

package com.secucard.connect.product.services.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.product.general.model.Location;

public class Address {

  @JsonProperty("postal_code")
  private Value postalCode;

  private Value country;

  private Value city;

  private Value street;

  @JsonProperty("street_number")
  private Value streetNumber;

  @JsonProperty("address_components")
  private AddressComponents[] addressComponents;

  @JsonProperty("address_formatted")
  private String addressFormatted;

  private Location geometry;

  public AddressComponents[] getAddressComponents() {
    return addressComponents;
  }

  public void setAddressComponents(AddressComponents[] addressComponents) { this.addressComponents = addressComponents; }

  public String getAddressFormatted() {
    return addressFormatted;
  }

  public void setAddressFormatted(String addressFormatted) {
    this.addressFormatted = addressFormatted;
  }

  public Location getGeometry() {
    return geometry;
  }

  public void setGeometry(Location geometry) {
    this.geometry = geometry;
  }

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
    return "Address{" + "zipcode=" + postalCode + ", country=" + country + ", city=" + city + ", street=" + street + ", streetNumber=" + streetNumber
        + ", addressComponents=" + addressComponents + ", addressFormatted=" + addressFormatted + ", geometry=" + geometry + '}';
  }
}