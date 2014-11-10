package com.secucard.connect.client;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.secucard.connect.SecuException;
import com.secucard.connect.channel.stomp.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

public class ClientConfiguration {
  private com.secucard.connect.channel.rest.Configuration restConfiguration;
  private com.secucard.connect.channel.stomp.Configuration stompConfiguration;
  private String defaultChannel;
  private int heartBeatSec;
  private boolean stompEnabled;

  private ClientConfiguration(Properties properties) {
    try {
      init(properties);
    } catch (Exception e) {
      throw new SecuException("Error loading configuration", e);
    }
  }

  private void init(Properties cfg) throws Exception {
    stompEnabled = Boolean.valueOf(cfg.getProperty("stompEnabled"));
    if (stompEnabled) {
      defaultChannel = cfg.getProperty("defaultChannel");
    } else {
      defaultChannel = ClientContext.REST;
    }

    heartBeatSec = new Integer(cfg.getProperty("heartBeatSec"));

    stompConfiguration = new Configuration(
        cfg.getProperty("stomp.host"),
        cfg.getProperty("stomp.virtualHost"),
        new Integer(cfg.getProperty("stomp.port")),
        cfg.getProperty("stomp.destination"),
        cfg.getProperty("stomp.user"),
        cfg.getProperty("stomp.password"),
        Boolean.valueOf(cfg.getProperty("stomp.receipt")),
        Boolean.valueOf(cfg.getProperty("stomp.ssl")),
        cfg.getProperty("stomp.replyQueue"),
        new Integer(cfg.getProperty("stomp.connTimeoutSec")),
        new Integer(cfg.getProperty("stomp.messageTimeoutSec")),
        new Integer(cfg.getProperty("stomp.maxMessageAgeSec")),
        new Integer(cfg.getProperty("stomp.socketTimeoutSec")),
        1000 * heartBeatSec);

    restConfiguration = new com.secucard.connect.channel.rest.Configuration(
        cfg.getProperty("rest.url"),
        cfg.getProperty("oauthUrl"),
        cfg.getProperty("clientId"),
        cfg.getProperty("clientSecret"));
  }

  private static Properties getDefaults() throws IOException {
    Properties defaultCfg = new Properties();
    InputStream stream = ClientConfiguration.class.getClassLoader().getResourceAsStream("default-config.properties");
    defaultCfg.load(stream);
    return defaultCfg;
  }

  public static ClientConfiguration getDefault() throws IOException {
    return new ClientConfiguration(getDefaults());
  }

  public static ClientConfiguration fromProperties(String path) throws IOException {
    Properties p = new Properties(getDefaults());
    p.load(new FileInputStream(path));
    return new ClientConfiguration(p);
  }

  public static ClientConfiguration fromJson(String path) throws IOException {
    HashMap hashMap = new ObjectMapper().readValue(new FileInputStream(path), HashMap.class);
    // todo: map to properties
    throw new UnsupportedOperationException();
  }


  public int getHeartBeatSec() {
    return heartBeatSec;
  }

  public boolean isStompEnabled() {
    return stompEnabled;
  }

  public final String getDefaultChannel() {
    return defaultChannel;
  }

  public final com.secucard.connect.channel.rest.Configuration getRestConfiguration() {
    return restConfiguration;
  }

  public final com.secucard.connect.channel.stomp.Configuration getStompConfiguration() {
    return stompConfiguration;
  }

}
