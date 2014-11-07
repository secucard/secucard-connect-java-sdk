package com.secucard.connect.stomp;

import com.secucard.connect.auth.OAuthClientCredentials;
import com.secucard.connect.auth.OAuthUserCredentials;

public class StompConfig {
  private final String host;
  private final int port;
  private final String password;
  private final String virtualHost;
  private final int heartbeatMs;
  private final boolean useReceipt;
  private final boolean useSsl;
  private final String userId;
  private final String replyQueue;
  private final int connectionTimeoutSec;
  private final int messagePollTimeoutSec;
  private final int maxMessageAgeSec;
  private final int socketTimeoutSec;
  private final String baseDestination;

  public StompConfig(String host, String virtualHost, int port, String baseDestination, String userId, String password,
                     int heartbeatMs, boolean useReceipt,
                     boolean useSsl, String replyQueue, int connectionTimeoutSec,
                     int messagePollTimeoutSec, int maxMessageAgeSec, int socketTimeoutSec) {
    this.host = host;
    this.port = port;
    this.password = password;
    this.virtualHost = virtualHost;
    this.heartbeatMs = heartbeatMs;
    this.useReceipt = useReceipt;
    this.useSsl = useSsl;
    this.userId = userId;
    this.replyQueue = replyQueue;
    this.connectionTimeoutSec = connectionTimeoutSec;
    this.messagePollTimeoutSec = messagePollTimeoutSec;
    this.maxMessageAgeSec = maxMessageAgeSec;
    this.baseDestination = baseDestination;
    this.socketTimeoutSec = socketTimeoutSec;
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

  public boolean isUseReceipt() {
    return useReceipt;
  }

  public boolean isUseSsl() {
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

  public int getMessagePollTimeoutSec() {
    return messagePollTimeoutSec;
  }

  public int getMaxMessageAgeSec() {
    return maxMessageAgeSec;
  }

  public String getBaseDestination() {
    return baseDestination;
  }

  public int getSocketTimeoutSec() {
    return socketTimeoutSec;
  }
}
