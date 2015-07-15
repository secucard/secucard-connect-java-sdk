package com.secucard.connect.net.stomp;

/**
 * STOMP protocol specific events.
 *
 * @see #STOMP_CONNECTED
 * @see #STOMP_DISCONNECTED
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

}
