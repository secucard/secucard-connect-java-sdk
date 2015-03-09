package com.secucard.connect;

import com.secucard.connect.model.transport.Status;

/**
 * This exception wraps business related errors thrown by the secucard server.
 * See the status field to get additional information.
 */
public class ProductException extends SecuException {

  public ProductException(Status status) {
    this.status = status;
  }

  public ProductException(Status status, Throwable cause) {
    super(cause);
    this.status = status;
  }

  @Override
  public String toString() {
    return "secucard server error: " + status + (getCause() == null ? "" : "; caused by: " + getCause().toString());
  }
}
