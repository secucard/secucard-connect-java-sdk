package com.secucard.connect.auth;

/**
 * Indicates that an authentication or authorization error happened.
 * Inspect {@link #getMessage()} {@link #getError()} and {@link #getCause()} to get more details.
 */
public class AuthException extends RuntimeException {
  String error;

  public String getError() {
    return error;
  }

  public AuthException(String error, String message) {
    super(message);
    this.error = error;
  }

  public AuthException(Throwable cause) {
    super(cause);
  }

  public AuthException(String message, Throwable cause) {
    super(message, cause);
  }


  public AuthException(String message) {
    super(message);
  }

}
