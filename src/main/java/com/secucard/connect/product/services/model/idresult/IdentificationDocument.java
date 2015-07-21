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

package com.secucard.connect.product.services.model.idresult;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IdentificationDocument {
  private Value country;

  @JsonProperty("dateissued")
  private Value dateIssued;

  @JsonProperty("issuedby")
  private Value issuedBy;

  private Value number;

  private Value type;

  @JsonProperty("validuntil")
  private Value validUntil;

  public Value getIssuedBy() {
    return issuedBy;
  }

  public void setIssuedBy(Value issuedBy) {
    this.issuedBy = issuedBy;
  }

  public Value getNumber() {
    return number;
  }

  public void setNumber(Value number) {
    this.number = number;
  }

  public Value getCountry() {
    return country;
  }

  public void setCountry(Value country) {
    this.country = country;
  }

  public Value getDateIssued() {
    return dateIssued;
  }

  public void setDateIssued(Value dateIssued) {
    this.dateIssued = dateIssued;
  }

  public Value getType() {
    return type;
  }

  public void setType(Value type) {
    this.type = type;
  }

  public Value getValidUntil() {
    return validUntil;
  }

  public void setValidUntil(Value validUntil) {
    this.validUntil = validUntil;
  }

  @Override
  public String toString() {
    return "IdentificationDocument{" +
        "country=" + country +
        ", dateIssued=" + dateIssued +
        ", issuedBy=" + issuedBy +
        ", number=" + number +
        ", type=" + type +
        ", validUntil=" + validUntil +
        '}';
  }
}
