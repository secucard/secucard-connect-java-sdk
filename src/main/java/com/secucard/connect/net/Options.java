package com.secucard.connect.net;

import com.secucard.connect.client.Callback;

/**
 * Holds all options for an API call.
 */
public class Options {
  public static final String CHANNEL_REST = "rest";
  public static final String CHANNEL_STOMP = "stomp";
  public boolean anonymous = false;
  public boolean expand = false;
  public boolean eventListening = false;
  public String channel;
  public String clientId = null;
  public Integer timeOutSec = null;

  /**
   * Set an callback to be executed after a resource was successfully retrieved.
   */
  public Callback.Notify<?> resultProcessing;

  public static Options getDefault() {
    return new Options();
  }

  public Options() {
  }

  public Options(String channel) {
    this.channel = channel;
  }
}
