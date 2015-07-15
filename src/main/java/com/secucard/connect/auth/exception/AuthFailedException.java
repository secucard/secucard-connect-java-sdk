package com.secucard.connect.auth.exception;

/**
 * Indicates an authorization attempt failed due missing or invalid authentication data.
 * Typically this kind of error is caused by wrong API usage or alike, something that is wrong implemented.
 * <p/>
 * Inspect {@link #getError()} for the general error "type". <br/>
 * Inspect {@link #getMessage()} for a more detailed description oft the error.
 */
public class AuthFailedException extends Exception {
  private String error;

  /**
   * Returns an error type string.
   */
  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public AuthFailedException(String message, String error) {
    super(message);
    this.error = error;
  }

  public AuthFailedException(String message) {
    super(message);
  }

  public AuthFailedException(String message, Throwable cause) {
    super(message, cause);
  }

  @Override
  public String getLocalizedMessage() {
    return error + ", " + getMessage();
  }
}
