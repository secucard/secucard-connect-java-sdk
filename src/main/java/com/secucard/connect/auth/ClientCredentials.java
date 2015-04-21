package com.secucard.connect.auth;

import java.util.Map;

public class ClientCredentials extends OAuthCredentials {
  private final String clientId;
  private final String clientSecret;

  public ClientCredentials(ClientCredentials clientCredentials) {
    this(clientCredentials.getClientId(), clientCredentials.getClientSecret());
    setDeviceId(clientCredentials.getDeviceId());
    setDeviceInfo(clientCredentials.getDeviceInfo());
  }

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
  public Map<String, Object> asMap() {
    Map<String, Object> map = super.asMap();
    map.put("client_id", clientId);
    map.put("client_secret", clientSecret);
    return map;
  }
}
