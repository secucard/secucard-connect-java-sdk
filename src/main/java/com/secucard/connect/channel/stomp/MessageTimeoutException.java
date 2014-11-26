package com.secucard.connect.channel.stomp;

public class MessageTimeoutException extends RuntimeException {
  public MessageTimeoutException(String message) {
    super(message);
  }

  public MessageTimeoutException() {
  }
}
