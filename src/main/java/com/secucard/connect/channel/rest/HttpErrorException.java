package com.secucard.connect.channel.rest;

import java.util.Map;

/**
 * Exception indicating a HTTP request failed.
 * The {@link #getHttpStatus()} returns the HTTP status code.
 */
public class HttpErrorException extends RuntimeException {
  private int httpStatus;
  private Map<String, String> entity;

  public Map<String, String> getEntity() {
    return entity;
  }

  public int getHttpStatus() {
    return httpStatus;
  }

  public HttpErrorException(int httpStatus, Map<String, String> entity) {
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

  public HttpErrorException(String message, Throwable cause, int httpStatus, Map<String, String> entity) {
    super(message, cause);
    this.httpStatus = httpStatus;
    this.entity = entity;
  }

  @Override
  public String toString() {
    return super.toString() + ": " + httpStatus;
  }
}
