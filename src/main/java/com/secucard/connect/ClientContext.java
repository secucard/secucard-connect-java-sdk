package com.secucard.connect;

import com.secucard.connect.channel.Channel;
import com.secucard.connect.storage.DataStorage;

public class ClientContext {
  private DataStorage dataStorage;
  private Channel restChannel;
  private Channel stompChannel;
  private ClientConfiguration config;
  public static final String STOMP = "stomp";
  public static final String REST = "rest";
  private String clientId;
  private ExceptionHandler exceptionHandler;
  private Object runtimeContext;

  public void setExceptionHandler(ExceptionHandler exceptionHandler) {
    this.exceptionHandler = exceptionHandler;
  }

  public ExceptionHandler getExceptionHandler() {
    return exceptionHandler;
  }

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

  public void setRestChannel(Channel restChannel) {
    this.restChannel = restChannel;
  }

  public void setStompChannel(Channel stompChannel) {
    this.stompChannel = stompChannel;
  }

  public ClientConfiguration getConfig() {
    return config;
  }

  public void setConfig(ClientConfiguration config) {
    this.config = config;
  }

  public Channel getRestChannel() {
    return restChannel;
  }

  public Channel getStompChannel() {
    return stompChannel;
  }

  public Object getRuntimeContext() {
    return runtimeContext;
  }

  public void setRuntimeContext(Object runtimeContext) {
    this.runtimeContext = runtimeContext;
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

