package com.secucard.connect.channel.rest;

import com.secucard.connect.auth.OAuthClientCredentials;
import com.secucard.connect.auth.OAuthUserCredentials;

public class Configuration {
  private final String baseUrl;
  private final String oauthUrl;
  private final OAuthClientCredentials clientCredentials;
  private OAuthUserCredentials userCredentials;

  public Configuration(String baseUrl, String oauthUrl, String clientId, String clientSecret) {
    this(baseUrl, oauthUrl, clientId, clientSecret, null, null);
  }

  private Configuration(String baseUrl, String oauthUrl, String clientId, String clientSecret, String user, String pwd) {
    this.baseUrl = baseUrl;
    this.oauthUrl = oauthUrl;
    this.clientCredentials = new OAuthClientCredentials(clientId, clientSecret);
    if (user != null && pwd != null) {
      this.userCredentials = new OAuthUserCredentials(user, pwd);
    }
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
