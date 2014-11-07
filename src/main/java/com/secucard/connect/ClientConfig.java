package com.secucard.connect;

import com.secucard.connect.rest.RestConfig;
import com.secucard.connect.stomp.StompConfig;

public class ClientConfig {
  private RestConfig restConfig;
  private StompConfig stompConfig;
  private BaseClient.ChannelName defaultChannel = Client.ChannelName.STOMP;


  public ClientConfig(RestConfig restConfig, StompConfig stompConfig) {
    this.restConfig = restConfig;
    this.stompConfig = stompConfig;
  }

  public BaseClient.ChannelName getDefaultChannel() {
    return defaultChannel;
  }

  public RestConfig getRestConfig() {
    return restConfig;
  }

  public StompConfig getStompConfig() {
    return stompConfig;
  }
}
