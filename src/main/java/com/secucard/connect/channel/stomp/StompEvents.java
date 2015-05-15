package com.secucard.connect.channel.stomp;

import java.util.Map;

/**
 * STOMP protocol specific events.
 *
 * @see #STOMP_CONNECTED
 * @see #STOMP_DISCONNECTED
 * @see StompEvents.AuthorizationFailed
 * @see StompEvents.Error
 */
public interface StompEvents {

  /**
   * Fired when connected to STOMP server.
   */
  public static final String STOMP_CONNECTED = "STOMP_CONNECTED";

  /**
   * Fired when disconnected from STOMP server.
   */
  public static final String STOMP_DISCONNECTED = "STOMP_DISCONNECTED";


  /**
   * Fired when given credentials could not be authorized, that is no access to a protected resource is given for
   * the provided credential. May happen on CONNECT or SEND when invalid credentials are provided in STOMP message
   * headers.
   */
  public static final class AuthorizationFailed {
    public String message;

    public AuthorizationFailed(String message) {
      this.message = message;
    }

    @Override
    public String toString() {
      return "AuthorizationFailed{" +
          "message='" + message + '\'' +
          '}';
    }
  }

  /**
   * Fired when a ERROR frame was received.
   */
  public static final class Error {
    public String message;
    public Map errorDetails;

    public Error(String message) {
      this.message = message;
    }

    public Error(String message, Map errorDetails) {
      this.message = message;
      this.errorDetails = errorDetails;
    }

    @Override
    public String toString() {
      return "Error{" +
          "message='" + message + '\'' +
          ", errorDetails=" + errorDetails +
          '}';
    }
  }
}
