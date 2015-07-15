package com.secucard.connect.net.rest;

/**
 * Exception indicating a HTTP request failed.
 * The {@link #getHttpStatus()} returns the HTTP status code.
 */
public class HttpErrorException extends Exception {
  private int httpStatus;
  private Object entity;

  public void setEntity(String entity) {
    this.entity = entity;
  }

  public Object getEntity() {
    return entity;
  }

  public int getHttpStatus() {
    return httpStatus;
  }

  public HttpErrorException(int httpStatus, Object entity) {
    this.httpStatus = httpStatus;
    this.entity = entity;
  }

  public HttpErrorException(int httpStatus) {
    this.httpStatus = httpStatus;
  }

  public HttpErrorException(Throwable cause, int httpStatus) {
    super(cause);
    this.httpStatus = httpStatus;
  }

  public HttpErrorException(String message, int httpStatus) {
    super(message);
    this.httpStatus = httpStatus;
  }

  public HttpErrorException(String message, Throwable cause, int httpStatus) {
    super(message, cause);
    this.httpStatus = httpStatus;
  }

  public HttpErrorException(String message, Throwable cause, int httpStatus, Object entity) {
    super(message, cause);
    this.httpStatus = httpStatus;
    this.entity = entity;
  }

  @Override
  public String toString() {
    return "HttpErrorException{" +
        "httpStatus=" + httpStatus +
        ", entity=" + entity +
        "} " + super.toString();
  }
}
