package com.secucard.connect.channel.stomp;

public class Configuration {
  private final String host;
  private final int port;
  private final String password;
  private final String virtualHost;
  private final int heartbeatMs;
  private final boolean useSsl;
  private final boolean autoConnect;
  private final String userId;
  private final String replyQueue;
  private final int connectionTimeoutSec;
  private final int messageTimeoutSec;
  private final int maxMessageAgeSec;
  private final int socketTimeoutSec;
  private final String basicDestination;

  public Configuration(String host, String virtualHost, int port,
                       String basicDestination,
                       String userId, String password,
                       boolean useSsl, String replyQueue, int connectionTimeoutSec,
                       int messageTimeoutSec, int maxMessageAgeSec, int socketTimeoutSec, int heartbeatMs) {
    this.host = host;
    this.port = port;
    this.password = password;
    this.virtualHost = virtualHost;
    this.heartbeatMs = heartbeatMs;
    this.useSsl = useSsl;
    this.userId = userId;
    this.replyQueue = replyQueue;
    this.connectionTimeoutSec = connectionTimeoutSec;
    this.messageTimeoutSec = messageTimeoutSec;
    this.maxMessageAgeSec = maxMessageAgeSec;
    this.socketTimeoutSec = socketTimeoutSec;
    this.autoConnect = true;

    if (!basicDestination.endsWith("/")) {
      basicDestination += "/";
    }
    this.basicDestination = basicDestination;
  }

  public boolean isAutoConnect() {
    return autoConnect;
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  public String getPassword() {
    return password;
  }

  public String getVirtualHost() {
    return virtualHost;
  }

  public int getHeartbeatMs() {
    return heartbeatMs;
  }

  public boolean useSsl() {
    return useSsl;
  }

  public String getUserId() {
    return userId;
  }

  public String getReplyQueue() {
    return replyQueue;
  }

  public int getConnectionTimeoutSec() {
    return connectionTimeoutSec;
  }

  public int getMessageTimeoutSec() {
    return messageTimeoutSec;
  }

  public int getMaxMessageAgeSec() {
    return maxMessageAgeSec;
  }

  public String getBasicDestination() {
    return basicDestination;
  }

  public int getSocketTimeoutSec() {
    return socketTimeoutSec;
  }
}
