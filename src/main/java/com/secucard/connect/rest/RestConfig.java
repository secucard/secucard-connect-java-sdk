package com.secucard.connect.rest;

import com.secucard.connect.auth.OAuthClientCredentials;

public class RestConfig {
  private final String baseUrl;
  private final String oauthUrl;
  private final OAuthClientCredentials clientCredentials;

  public RestConfig(String baseUrl, String oauthUrl, OAuthClientCredentials clientCredentials) {
    this.baseUrl = baseUrl;
    this.oauthUrl = oauthUrl;
    this.clientCredentials = clientCredentials;
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
