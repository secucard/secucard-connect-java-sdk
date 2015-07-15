package com.secucard.connect.net;


import com.secucard.connect.product.common.model.Status;

/**
 * Wraps an exception thrown by the API server, holds defined cause.
 */
public class ServerErrorException extends RuntimeException {
  private Status status;

  public Status getStatus() {
    return status;
  }

  public ServerErrorException(Status status) {
    this.status = status;
  }
}
