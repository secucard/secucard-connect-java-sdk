package com.secucard.connect.channel.stomp;

public class ConnectionTimeoutException extends RuntimeException {
  public ConnectionTimeoutException() {
  }

  public ConnectionTimeoutException(String message) {
    super(message);
  }
}
