package com.secucard.connect.event;

public class Events {
  public static final String CONNECTED = "CONNECTED";
  public static final String DISCONNECTED = "DISCONNECTED";
  public static final String ANY = "*";

  /**
   * Event listener registered for this type gets notified when the connection state of the client changes.
   */
  public static class ConnectionStateChanged {
    public boolean connected;

    public ConnectionStateChanged(boolean connected) {
      this.connected = connected;
    }
  }
}
