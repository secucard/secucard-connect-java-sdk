package com.secucard.connect.auth;

import java.util.Map;

public class AppUserCredentials extends ClientCredentials {
  private String userName;
  private String password;

  /**
   * A unique device id like UUID. May be optional for some credential types.
   */
  protected String deviceId;

  public AppUserCredentials(String clientId, String clientSecret, String userName, String password, String deviceId) {
    super(clientId, clientSecret);
    this.userName = userName;
    this.password = password;
    this.deviceId = deviceId;
  }

  public String getUserName() {
    return userName;
  }

  public String getPassword() {
    return password;
  }

  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }

  public String getDeviceId() {
    return deviceId;
  }

  @Override
  public String getGrantType() {
    return "appuser";
  }

  @Override
  public String getId() {
    return getGrantType() + clientId + clientSecret + userName + password + (deviceId == null ? "" : deviceId);
  }

  @Override
  public Map<String, Object> asMap() {
    Map<String, Object> map = super.asMap();
    map.put("username", userName);
    map.put("password", password);
    map.put("device", deviceId);
    return map;
  }


}
