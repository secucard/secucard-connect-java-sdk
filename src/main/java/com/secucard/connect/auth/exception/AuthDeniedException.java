package com.secucard.connect.auth.exception;

/**
 * Indicates an authentication is denied due wrong credentials. All needed data are present but
 * This kind of error could usually be resolved by trying again with correct data.
 * <p/>
 * The {@link #getMessage()} may provide additional details.
 */
public class AuthDeniedException extends Exception {
  public AuthDeniedException() {
  }

  public AuthDeniedException(String message) {
    super(message);
  }
}
