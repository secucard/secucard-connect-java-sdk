package com.secucard.connect.product.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Status {

  private String status;

  // the error class
  private String error;

  //the error message
  @JsonProperty("error_details")
  private String errorDetails;

  // a error message for the user
  @JsonProperty("error_user")
  private String errorUser;

  // the unique error code
  private String code;

  // a unique error id support purposes
  private String supportId;

  public Status() {
  }

  public Status(String status, String error, String errorDetails) {
    this.status = status;
    this.error = error;
    this.errorDetails = errorDetails;
  }

  public Status(Status other) {
    this.status = other.status;
    this.error = other.error;
    this.errorDetails = other.errorDetails;
    this.errorUser = other.errorUser;
    this.code = other.code;
    this.supportId = other.supportId;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public String getErrorDetails() {
    return errorDetails;
  }

  public void setErrorDetails(String errorDetails) {
    this.errorDetails = errorDetails;
  }

  public String getErrorUser() {
    return errorUser;
  }

  public void setErrorUser(String errorUser) {
    this.errorUser = errorUser;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getSupportId() {
    return supportId;
  }

  public void setSupportId(String supportId) {
    this.supportId = supportId;
  }

  @Override
  public String toString() {
    return "Status{" +
        "status='" + status + '\'' +
        ", error='" + error + '\'' +
        ", errorDetails='" + errorDetails + '\'' +
        ", errorUser='" + errorUser + '\'' +
        ", code='" + code + '\'' +
        ", supportId='" + supportId + '\'' +
        '}';
  }
}
