package com.secucard.connect.channel.rest;

import com.secucard.connect.auth.OAuthClientCredentials;

public class Configuration {
  private final String baseUrl;
  private final String oauthUrl;
  private final OAuthClientCredentials clientCredentials;

  public Configuration(String baseUrl, String oauthUrl,String clientId, String clientSecret) {
    this.baseUrl = baseUrl;
    this.oauthUrl = oauthUrl;
    this.clientCredentials = new OAuthClientCredentials(clientId, clientSecret);
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
}
