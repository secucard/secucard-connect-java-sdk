package com.secucard.connect;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.secucard.connect.auth.ClientCredentials;
import com.secucard.connect.auth.UserCredentials;
import com.secucard.connect.channel.stomp.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

/**
 * Configuration data of the client.
 */
public class ClientConfiguration {
  private com.secucard.connect.channel.rest.Configuration restConfiguration;
  private com.secucard.connect.channel.stomp.Configuration stompConfiguration;
  private String defaultChannel;
  private int heartBeatSec;
  private boolean stompEnabled;
  private String cacheDir;
  private boolean androidMode;
  private int authWaitTimeoutSec;
  private String oauthUrl;
  private ClientCredentials clientCredentials;
  private UserCredentials userCredentials;
  private String deviceId;
  private String authType;

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

    heartBeatSec = Integer.valueOf(cfg.getProperty("heartBeatSec"));
    cacheDir = cfg.getProperty("cacheDir");
    authWaitTimeoutSec = Integer.valueOf(cfg.getProperty("auth.waitTimeoutSec"));
    oauthUrl = cfg.getProperty("auth.oauthUrl");
    clientCredentials = new ClientCredentials(cfg.getProperty("auth.clientId"), cfg.getProperty("auth.clientSecret"));
    deviceId = cfg.getProperty("device");
    authType = cfg.getProperty("auth.type");
    androidMode = Boolean.valueOf(cfg.getProperty("androidMode"));

    stompConfiguration = new Configuration(
        cfg.getProperty("stomp.host"),
        cfg.getProperty("stomp.virtualHost"),
        Integer.valueOf(cfg.getProperty("stomp.port")),
        cfg.getProperty("stomp.destination"),
        cfg.getProperty("stomp.user"),
        cfg.getProperty("stomp.password"),
        Boolean.valueOf(cfg.getProperty("stomp.ssl")),
        cfg.getProperty("stomp.replyQueue"),
        Integer.valueOf(cfg.getProperty("stomp.connTimeoutSec")),
        Integer.valueOf(cfg.getProperty("stomp.messageTimeoutSec")),
        Integer.valueOf(cfg.getProperty("stomp.maxMessageAgeSec")),
        Integer.valueOf(cfg.getProperty("stomp.socketTimeoutSec")),
        1000 * heartBeatSec);

    restConfiguration = new com.secucard.connect.channel.rest.Configuration(
        cfg.getProperty("rest.url"));
  }

  public void setUserCredentials(UserCredentials userCredentials) {
    this.userCredentials = userCredentials;
  }

  public void setClientCredentials(ClientCredentials clientCredentials) {
    this.clientCredentials = clientCredentials;
  }

  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
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
   * @param path The file path. If this path is relative (no first "/") it will be treated a a classpath relative path.
   * @return Configuration instance.
   * @throws IOException If a error occurs.
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
    Properties defaults = getDefaults();  // must provide default properties
    Properties p = new Properties(defaults);
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

  public String getCacheDir() {
    return cacheDir;
  }

  public final com.secucard.connect.channel.rest.Configuration getRestConfiguration() {
    return restConfiguration;
  }

  public final com.secucard.connect.channel.stomp.Configuration getStompConfiguration() {
    return stompConfiguration;
  }

  public boolean isAndroidMode() {
    return androidMode;
  }

  public int getAuthWaitTimeoutSec() {
    return authWaitTimeoutSec;
  }

  public String getOauthUrl() {
    return oauthUrl;
  }

  public ClientCredentials getClientCredentials() {
    return clientCredentials;
  }

  public UserCredentials getUserCredentials() {
    return userCredentials;
  }

  public String getDeviceId() {
    return deviceId;
  }

  public String getAuthType() {
    return authType;
  }
}
