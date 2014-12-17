package com.secucard.connect;

public class ConnectionException extends  RuntimeException {

  public ConnectionException() {
    super();
  }

  public ConnectionException(String message) {
    super(message);
  }

  public ConnectionException(String message, Throwable cause) {
    super(message, cause);
  }

  public ConnectionException(Throwable cause) {
    super(cause);
  }
}
