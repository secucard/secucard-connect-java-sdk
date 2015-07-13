package com.secucard.connect;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.secucard.connect.auth.ClientCredentials;
import com.secucard.connect.channel.Channel;
import com.secucard.connect.channel.rest.RestChannelBase;
import com.secucard.connect.channel.stomp.StompChannel;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

/**
 * Configuration data of the client.
 */
public class ClientConfiguration {
  private final RestChannelBase.Configuration restConfiguration;
  private final StompChannel.Configuration stompConfiguration;
  private final String defaultChannel;
  private final int heartBeatSec;
  private final boolean stompEnabled;
  private final String cacheDir;
  private final boolean androidMode;
  private final int authWaitTimeoutSec;
  private final String oauthUrl;
  private final ClientCredentials clientCredentials;
  private final String deviceId;
  private final String authType;
  private final String logFormat;
  private final String logLevel;
  private final String logPath;
  private final int logLimit;
  private final int logCount;
  private final boolean logIgnoreGlobal;

  private ClientConfiguration(Properties properties) {
    try {
      stompEnabled = Boolean.valueOf(properties.getProperty("stompEnabled"));
      if (stompEnabled) {
        defaultChannel = properties.getProperty("defaultChannel").toUpperCase();
      } else {
        defaultChannel = Channel.REST;
      }

      heartBeatSec = Integer.valueOf(properties.getProperty("heartBeatSec"));
      cacheDir = properties.getProperty("cacheDir");
      authWaitTimeoutSec = Integer.valueOf(properties.getProperty("auth.waitTimeoutSec"));
      oauthUrl = properties.getProperty("auth.oauthUrl");
      clientCredentials = new ClientCredentials(properties.getProperty("auth.clientId"), properties.getProperty("auth.clientSecret"));
      deviceId = properties.getProperty("device");
      authType = properties.getProperty("auth.type");
      androidMode = Boolean.valueOf(properties.getProperty("androidMode"));
      logIgnoreGlobal = Boolean.valueOf(properties.getProperty("logging.local"));
      logCount = Integer.valueOf(properties.getProperty("logging.count"));
      logLimit = Integer.valueOf(properties.getProperty("logging.limit"));
      logPath = properties.getProperty("logging.path");
      logLevel = properties.getProperty("logging.level");
      logFormat = properties.getProperty("logging.format");

      stompConfiguration = new StompChannel.Configuration(
          properties.getProperty("stomp.host"),
          properties.getProperty("stomp.virtualHost"),
          Integer.valueOf(properties.getProperty("stomp.port")),
          properties.getProperty("stomp.destination"),
          properties.getProperty("stomp.user"),
          properties.getProperty("stomp.password"),
          Boolean.valueOf(properties.getProperty("stomp.ssl")),
          properties.getProperty("stomp.replyQueue"),
          Integer.valueOf(properties.getProperty("stomp.connTimeoutSec")),
          Integer.valueOf(properties.getProperty("stomp.messageTimeoutSec")),
          Integer.valueOf(properties.getProperty("stomp.maxMessageAgeSec")),
          Integer.valueOf(properties.getProperty("stomp.socketTimeoutSec")),
          1000 * heartBeatSec,
          Boolean.valueOf(properties.getProperty("stomp.disconnectOnError")));

      restConfiguration = new RestChannelBase.Configuration(properties.getProperty("rest.url"),
          Integer.valueOf(properties.getProperty("rest.responseTimeoutSec")));
    } catch (Exception e) {
      throw new IllegalStateException("Can't load client configuration.", e);
    }
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

  public final RestChannelBase.Configuration getRestConfiguration() {
    return restConfiguration;
  }

  public final StompChannel.Configuration getStompConfiguration() {
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

  public String getDeviceId() {
    return deviceId;
  }

  public String getAuthType() {
    return authType;
  }

  public String getLogFormat() {
    return logFormat;
  }

  public String getLogLevel() {
    return logLevel;
  }

  public String getLogPath() {
    return logPath;
  }

  public int getLogLimit() {
    return logLimit;
  }

  public int getLogCount() {
    return logCount;
  }

  public boolean isLogIgnoreGlobal() {
    return logIgnoreGlobal;
  }
}
