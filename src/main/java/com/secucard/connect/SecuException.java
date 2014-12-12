package com.secucard.connect;

public class SecuException extends RuntimeException {

  public SecuException() {
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
