package com.secucard.connect.event;

/**
 * Defines client common events and event related constants.
 */
public abstract class Events {

  // common event types
  public static final String TYPE_CHANGED = "changed";
  public static final String TYPE_ADDED = "added";
  public static final String TYPE_DISPLAY = "display";

  /**
   * Fired when the connection state of the client changes.
   */
  public static final class ConnectionStateChanged {
    public boolean connected;

    public ConnectionStateChanged(boolean connected) {
      this.connected = connected;
    }
  }


}
