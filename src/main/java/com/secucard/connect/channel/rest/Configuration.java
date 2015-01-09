package com.secucard.connect.channel.rest;

import com.secucard.connect.auth.OAuthClientCredentials;
import com.secucard.connect.auth.OAuthUserCredentials;

public class Configuration {
  private final String baseUrl;
  private final String oauthUrl;
  private final OAuthClientCredentials clientCredentials;
  private OAuthUserCredentials userCredentials;
  private String deviceId;

  public Configuration(String baseUrl, String oauthUrl, String clientId, String clientSecret, String deviceId) {
    this(baseUrl, oauthUrl, clientId, clientSecret, null, null, deviceId);
  }

  private Configuration(String baseUrl, String oauthUrl, String clientId, String clientSecret, String user, String pwd,
                        String deviceId) {
    this.baseUrl = baseUrl;
    this.oauthUrl = oauthUrl;
    this.clientCredentials = new OAuthClientCredentials(clientId, clientSecret);
    if (user != null && pwd != null) {
      this.userCredentials = new OAuthUserCredentials(user, pwd);
    }
    this.deviceId = deviceId;
  }

  public String getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  public String getOauthUrl() {
    return oauthUrl;
  }

  public OAuthClientCredentials getClientCredentials() {
    return clientCredentials;
  }

  public OAuthUserCredentials getUserCredentials() {
    return userCredentials;
  }

  public void setUserCredentials(OAuthUserCredentials userCredentials) {
    this.userCredentials = userCredentials;
  }
}
