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

package com.secucard.connect.product.services.model.common;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserData {
  @JsonProperty("dob")
  private Value dateOfBirth;

  private Value forename;

  private Value surname;

  private Address address;

  private Value birthplace;

  private Value nationality;

  private Value gender;

  public Value getGender() {
    return gender;
  }

  public void setGender(Value gender) {
    this.gender = gender;
  }

  public Value getDateOfBirth() {
    return dateOfBirth;
  }

  public void setDateOfBirth(Value dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
  }

  public Value getForename() {
    return forename;
  }

  public void setForename(Value forename) {
    this.forename = forename;
  }

  public Value getSurname() {
    return surname;
  }

  public void setSurname(Value surname) {
    this.surname = surname;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public Value getBirthplace() {
    return birthplace;
  }

  public void setBirthplace(Value birthplace) {
    this.birthplace = birthplace;
  }

  public Value getNationality() {
    return nationality;
  }

  public void setNationality(Value nationality) {
    this.nationality = nationality;
  }

  @Override
  public String toString() {
    return "UserData{" +
        "birthday=" + dateOfBirth +
        ", firstname=" + forename +
        ", lastname=" + surname +
        ", address=" + address +
        ", birthplace=" + birthplace +
        ", nationality=" + nationality +
        ", gender=" + gender +
        '}';
  }
}
