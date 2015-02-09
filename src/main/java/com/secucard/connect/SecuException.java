package com.secucard.connect;

import com.secucard.connect.model.transport.Status;

public class SecuException extends RuntimeException {
  private Status status;

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

}
