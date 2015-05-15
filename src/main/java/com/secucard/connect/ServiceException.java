package com.secucard.connect;

/**
 * Encapsulates any service error.
 */
public class ServiceException extends RuntimeException {
  public ServiceException() {
  }

  public ServiceException(String message) {
    super(message);
  }

  public ServiceException(String message, Throwable cause) {
    super(message, cause);
  }

  public ServiceException(Throwable cause) {
    super(cause);
  }
}
