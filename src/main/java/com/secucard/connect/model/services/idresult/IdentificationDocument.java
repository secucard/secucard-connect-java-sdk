package com.secucard.connect.model.services.idresult;

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
