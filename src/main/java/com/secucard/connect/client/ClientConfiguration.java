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
  private String storagePath;
  private boolean autoConnect; // todo: not supported yet

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
    storagePath = cfg.getProperty("storagePath");

    stompConfiguration = new Configuration(
        cfg.getProperty("stomp.host"),
        cfg.getProperty("stomp.virtualHost"),
        new Integer(cfg.getProperty("stomp.port")),
        cfg.getProperty("stomp.destination"),
        cfg.getProperty("stomp.user"),
        cfg.getProperty("stomp.password"),
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

  /**
   * Load the default configuration from file default-config.properties.
   *
   * @return
   * @throws IOException
   */
  private static Properties getDefaults() throws IOException {
    Properties defaultCfg = new Properties();
    InputStream stream = ClientConfiguration.class.getClassLoader().getResourceAsStream("default-config.properties");
    defaultCfg.load(stream);
    return defaultCfg;
  }

  public static ClientConfiguration getDefault() throws IOException {
    return new ClientConfiguration(getDefaults());
  }

  /**
   * Create configuration from file.
   *
   * @param path The file path. If this path is relative (no first "/") it will be treatet a a classpath relative path.
   * @return Configuration instance.
   * @throws IOException If a error ocurrs.
   */
  public static ClientConfiguration fromProperties(String path) throws IOException {
    InputStream inputStream;
    if (path.startsWith("/")) {
      // absolute path
      inputStream = new FileInputStream(path);
    } else {
      // relative path, treat as classpath relative
      inputStream = ClientConfiguration.class.getClassLoader().getResourceAsStream(path);
    }
    return fromStream(inputStream);
  }

  public static ClientConfiguration fromJson(String path) throws IOException {
    HashMap hashMap = new ObjectMapper().readValue(new FileInputStream(path), HashMap.class);
    // todo: map to properties
    throw new UnsupportedOperationException();
  }

  public static ClientConfiguration fromStream(InputStream inputStream) throws IOException {
    Properties p = new Properties();
    p.load(inputStream);
    return new ClientConfiguration(p);
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

  public String getStoragePath() {
    return storagePath;
  }

  public final com.secucard.connect.channel.rest.Configuration getRestConfiguration() {
    return restConfiguration;
  }

  public final com.secucard.connect.channel.stomp.Configuration getStompConfiguration() {
    return stompConfiguration;
  }

}
