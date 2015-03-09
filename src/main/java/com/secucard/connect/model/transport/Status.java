package com.secucard.connect.model.transport;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Status {

  private String status;

  private String error;

  @JsonProperty("error_details")
  private String errorDetails;

  @JsonProperty("error_description")
  private String errorDescription;

  @JsonProperty("error_user")
  private String errorUser;

  private String code;

  private String supportId;

  public Status() {
  }

  public Status(String status, String error, String errorDetails) {
    this.status = status;
    this.error = error;
    this.errorDetails = errorDetails;
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

  public String getErrorDescription() {
    return errorDescription;
  }

  public void setErrorDescription(String errorDescription) {
    this.errorDescription = errorDescription;
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
        ", errorDescription='" + errorDescription + '\'' +
        ", errorUser='" + errorUser + '\'' +
        ", code='" + code + '\'' +
        ", supportId='" + supportId + '\'' +
        '}';
  }
}
