package com.secucard.connect.auth;

import com.secucard.connect.model.transport.Status;

/**
 * Indicates that an authentication or authorization error happened.
 * Get details from status property.
 */
public class AuthException extends RuntimeException {
  private Status status;

  public Status getStatus() {
    return status;
  }

  public AuthException(String message, Throwable cause) {
    super(message, cause);
  }

  public AuthException(Status status) {
    this.status = status;
  }

  public AuthException(String message) {
    super(message);
  }

  public AuthException(String message, Status status) {
    super(message);
    this.status = status;
  }

  public AuthException(String message, Throwable cause, Status status) {
    super(message, cause);
    this.status = status;
  }

  public AuthException(Throwable cause, Status status) {
    super(cause);
    this.status = status;
  }

  @Override
  public String toString() {
    return super.toString() + (status == null ? "" : ": " + status);
  }
}
