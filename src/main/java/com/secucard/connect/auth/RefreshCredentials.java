package com.secucard.connect.auth;

import java.util.Map;

public class RefreshCredentials extends ClientCredentials {
  private String refreshToken;

  public RefreshCredentials(ClientCredentials clientCredentials, String refreshToken) {
    super(clientCredentials);
    this.refreshToken = refreshToken;
  }

  public RefreshCredentials(String clientId, String clientSecret) {
    super(clientId, clientSecret);
  }

  public RefreshCredentials(String clientId, String clientSecret, String refreshToken) {
    super(clientId, clientSecret);
    this.refreshToken = refreshToken;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  @Override
  public String getGrantType() {
    return "refresh_token";
  }

  @Override
  public Map<String, Object> asMap() {
    Map<String, Object> map = super.asMap();
    map.put("refresh_token", refreshToken);
    return map;
  }
}
