package com.secucard.connect.model.services.idresult;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IdentificationDocument {
  private Value country;

  @JsonProperty("dateissued")
  private Value dateIssued;

  private Value type;

  @JsonProperty("validuntil")
  private Value validUntil;

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
    return "IndentificationDocument{" +
        "country=" + country +
        ", dateIssued=" + dateIssued +
        ", type=" + type +
        ", validUntil=" + validUntil +
        '}';
  }
}
