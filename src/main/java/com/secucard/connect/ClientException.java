package com.secucard.connect;

/**
 * General client exception.
 * Calling {@link #getCause()} or {@link #getMessage()} may provide further details.
 */
public class ClientException extends RuntimeException {

  public ClientException() {
  }

  public ClientException(String message) {
    super(message);
  }

  public ClientException(String message, Throwable cause) {
    super(message, cause);
  }

  public ClientException(Throwable cause) {
    super(cause);
  }

}
