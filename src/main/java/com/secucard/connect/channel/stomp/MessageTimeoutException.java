package com.secucard.connect.channel.stomp;

/**
 * Indicates that a STOMP message was not received in time.
 * After a certain wait time this exception is thrown.
 */
public class MessageTimeoutException extends RuntimeException {
  public MessageTimeoutException(String message) {
    super(message);
  }

  public MessageTimeoutException() {
  }
}
