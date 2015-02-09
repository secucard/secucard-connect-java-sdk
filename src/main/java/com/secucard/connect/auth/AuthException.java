package com.secucard.connect.auth;

/**
 * Exception thrown when authentication, authorization errors happen.
 */
public class AuthException extends RuntimeException {
  public AuthException() {
    super();
  }

  public AuthException(String message) {
    super(message);
  }

  public AuthException(String message, Throwable cause) {
    super(message, cause);
  }

  public AuthException(Throwable cause) {
    super(cause);
  }
}
