package com.secucard.connect.rest;

public class RestConfig {
  private final String baseUrl;
  private final String oauthUrl;

  public RestConfig(String baseUrl, String oauthUrl) {
    this.baseUrl = baseUrl;
    this.oauthUrl = oauthUrl;
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  public String getOauthUrl() {
    return oauthUrl;
  }
}
