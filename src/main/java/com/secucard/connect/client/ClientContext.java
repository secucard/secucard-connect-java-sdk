package com.secucard.connect.client;

import com.secucard.connect.channel.Channel;
import com.secucard.connect.channel.rest.RestChannel;
import com.secucard.connect.channel.stomp.SecuStompChannel;
import com.secucard.connect.storage.DataStorage;

public class ClientContext {
  private DataStorage dataStorage;
  private RestChannel restChannel;
  private SecuStompChannel stompChannel;
  private ClientConfiguration config;
  public static final String STOMP = "stomp";
  public static final String REST = "rest";
  private String clientId;

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public DataStorage getDataStorage() {
    return dataStorage;
  }

  public void setDataStorage(DataStorage dataStorage) {
    this.dataStorage = dataStorage;
  }

  public void setRestChannel(RestChannel restChannel) {
    this.restChannel = restChannel;
  }

  public void setStompChannel(SecuStompChannel stompChannel) {
    this.stompChannel = stompChannel;
  }

  public ClientConfiguration getConfig() {
    return config;
  }

  public void setConfig(ClientConfiguration config) {
    this.config = config;
  }

  public Channel getChannnel() {
    return getChannnel(config.getDefaultChannel());
  }

  public RestChannel getRestChannel() {
    return restChannel;
  }

  public SecuStompChannel getStompChannel() {
    return stompChannel;
  }

  public Channel getChannnel(String name) {
    if (name.equals(STOMP)) {
      return stompChannel;
    }

    if (name.equals(REST)) {
      return restChannel;
    }
    return null;
  }
}

