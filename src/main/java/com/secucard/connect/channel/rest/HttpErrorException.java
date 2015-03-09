package com.secucard.connect.channel.rest;

public class HttpErrorException extends RuntimeException {
  private int httpStatus;

  public int getHttpStatus() {
    return httpStatus;
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


  @Override
  public String toString() {
    return super.toString() + ": " + httpStatus;
  }
}
