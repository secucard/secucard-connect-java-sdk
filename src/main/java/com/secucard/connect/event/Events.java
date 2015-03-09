package com.secucard.connect.event;

import java.util.Map;

public class Events {

  // string events
  public static final String STOMP_CONNECTED = "STOMP_CONNECTED";
  public static final String STOMP_DISCONNECTED = "STOMP_DISCONNECTED";
  public static final String ANY = "*";

  // event types
  public static final String TYPE_CHANGED = "changed";
  public static final String TYPE_DISPLAY = "display";

  /**
   * Event listener registered for this type gets notified when the connection state of the client changes.
   */
  public static class ConnectionStateChanged {
    public boolean connected;

    public ConnectionStateChanged(boolean connected) {
      this.connected = connected;
    }
  }

  public static class AuthorizationFailed {
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

  public static class Error {
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
