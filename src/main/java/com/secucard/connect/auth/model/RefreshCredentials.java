package com.secucard.connect.auth.model;

import java.util.Map;

public class RefreshCredentials extends ClientCredentials {
  private String refreshToken;

  public RefreshCredentials(String clientId, String clientSecret, String refreshToken) {
    super(clientId, clientSecret);
    this.refreshToken = refreshToken;
  }

  public RefreshCredentials(ClientCredentials clientCredentials, String refreshToken) {
    this(clientCredentials.getClientId(),clientCredentials.getClientSecret(), refreshToken);
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  @Override
  public String getGrantType() {
    return "refresh_token";
  }

  @Override
  public String getId() {
    // can't be compared
    throw new UnsupportedOperationException();
  }

  @Override
  public Map<String, Object> asMap() {
    Map<String, Object> map = super.asMap();
    map.put("refresh_token", refreshToken);
    return map;
  }
}
