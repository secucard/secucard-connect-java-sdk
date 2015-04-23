package com.secucard.connect.auth;

import java.util.Map;

public class ClientCredentials extends OAuthCredentials {
  protected final String clientId;
  protected final String clientSecret;

  public ClientCredentials(String clientId, String clientSecret) {
    this.clientId = clientId;
    this.clientSecret = clientSecret;
  }

  public String getClientId() {
    return clientId;
  }

  public String getClientSecret() {
    return clientSecret;
  }

  @Override
  public String getGrantType() {
    return "client_credentials";
  }

  @Override
  public String getId() {
    return getGrantType() + clientId + clientSecret;
  }

  @Override
  public Map<String, Object> asMap() {
    Map<String, Object> map = super.asMap();
    map.put("client_id", clientId);
    map.put("client_secret", clientSecret);
    return map;
  }
}
