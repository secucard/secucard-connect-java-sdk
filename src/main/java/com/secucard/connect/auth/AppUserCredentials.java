package com.secucard.connect.auth;

import java.util.Map;

public class AppUserCredentials extends ClientCredentials {
  private String userName;
  private String password;

  public AppUserCredentials(String clientId, String clientSecret) {
    super(clientId, clientSecret);
  }

  public AppUserCredentials(String clientId, String clientSecret, String userName, String password) {
    super(clientId, clientSecret);
    this.userName = userName;
    this.password = password;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public String getGrantType() {
    return "appuser";
  }

  @Override
  public Map<String, Object> asMap() {
    Map<String, Object> map = super.asMap();
    map.put("username", userName);
    map.put("password", password);
    if (deviceId != null) {
      map.put("device", deviceId);
    }
    if (deviceInfo != null && !deviceInfo.isEmpty()) {
      map.putAll(deviceInfo);
    }
    return map;
  }
}
