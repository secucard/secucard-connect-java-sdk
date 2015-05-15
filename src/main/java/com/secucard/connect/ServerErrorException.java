package com.secucard.connect;

import com.secucard.connect.model.transport.Status;

/**
 * This exception wraps business related errors thrown by the secucard server.
 * The status field may contain additional error details.
 */
public class ServerErrorException extends RuntimeException {

  /**
   * Detailed error status.
   */
  protected Status status;

  public Status getStatus() {
    return status;
  }

  public ServerErrorException(String message) {
    super(message);
  }

  public ServerErrorException(Status status) {
    this.status = status;
  }

  public ServerErrorException(Status status, Throwable cause) {
    super(cause);
    this.status = status;
  }

  @Override
  public String toString() {
    if (status == null) {
      return super.toString();
    }
    return getClass().getName() + ": " + status.toString();
  }
}
