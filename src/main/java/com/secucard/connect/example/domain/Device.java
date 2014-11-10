package com.secucard.connect.example.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.client.BaseClient;
import com.secucard.connect.client.ClientConfiguration;
import com.secucard.connect.client.ConnectException;
import com.secucard.connect.model.SecuObject;

public class Device extends SecuObject {
  public static final String OBJECT = "smart.devices";

  @JsonProperty
  private String type;

  private BaseClient client;

  private Device() {
  }

  public void setClient(BaseClient client) {
    this.client = client;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public String getObject() {
    return OBJECT;
  }

  public boolean register() {
    return client.getContext().getStompChannel().execute("register", new String[]{getId()}, this, null);
  }

  public static Device create(ClientConfiguration config){
    Device d = new Device();
//    d.setClient(BaseClient.create(config, BaseClient.class));
    return d;
  }

  public void connect() throws ConnectException {
    client.connect();
  }

  public void disconnect() {
    client.disconnect();
  }

}
