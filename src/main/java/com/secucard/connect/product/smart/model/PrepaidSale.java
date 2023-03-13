package com.secucard.connect.product.smart.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PrepaidSale {

  private int id;

  private String status;

  @JsonProperty("error_details")
  private String errorDetails;

  public int getId() { return id; }

  public void setId(int id) { this.id = id; }

  public String getStatus() { return status; }

  public void setStatus(String status) { this.status = status; }

  public String getErrorDetails() { return errorDetails; }

  public void setErrorDetails(String errorDetails) {
    this.errorDetails = errorDetails;
  }

  @Override
  public String toString() {
    return "PrepaidSale{"
      + "id='" + id + "'"
      + ", status='" + status
      + ", error_details='" + errorDetails
      + "'}";
  }
}
