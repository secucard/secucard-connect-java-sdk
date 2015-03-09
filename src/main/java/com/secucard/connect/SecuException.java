package com.secucard.connect;

import com.secucard.connect.model.transport.Status;

/**
 * General, basic client exception.
 * Additional information may be contained in status field.
 */
public class SecuException extends RuntimeException {

  /**
   * Detailed error status.
   */
  protected Status status;

  public SecuException(Status status, Throwable throwable) {
    super(throwable);
    this.status = status;
  }

  public Status getStatus() {
    return status;
  }

  public SecuException() {
  }

  public SecuException(Status status) {
    this.status = status;
  }

  public SecuException(String message) {
    super(message);
  }

  public SecuException(String message, Throwable cause) {
    super(message, cause);
  }

  public SecuException(Throwable cause) {
    super(cause);
  }


  @Override
  public String toString() {
    return ": " + status + "; " + super.toString();
  }
}
