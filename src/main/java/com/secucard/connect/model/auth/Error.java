package com.secucard.connect.model.auth;

/**
 * Error response body for OAuth.
 */
public class Error {
  private String error;

  private String errorDescription;

  private int status;

  public Error(String error, String errorDescription, int status) {
    this.error = error;
    this.errorDescription = errorDescription;
    this.status = status;
  }

  public String getError() {
    return error;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public void setError(String error) {
    this.error = error;
  }

  public String getErrorDescription() {
    return errorDescription;
  }

  public void setErrorDescription(String errorDescription) {
    this.errorDescription = errorDescription;
  }

  @Override
  public String toString() {
    return "Error{" +
        "error='" + error + '\'' +
        ", errorDescription='" + errorDescription + '\'' +
        ", status=" + status +
        '}';
  }
}
