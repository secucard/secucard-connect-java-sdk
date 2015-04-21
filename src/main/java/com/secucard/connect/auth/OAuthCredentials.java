package com.secucard.connect.auth;

import java.util.HashMap;
import java.util.Map;

public abstract class OAuthCredentials {
  protected String deviceId;
  protected Map<String, String> deviceInfo;

  public String getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }

  public Map<String, String> getDeviceInfo() {
    return deviceInfo;
  }

  public void setDeviceInfo(Map<String, String> deviceInfo) {
    this.deviceInfo = deviceInfo;
  }

  public abstract String getGrantType();

  public Map<String, Object> asMap() {
    HashMap<String, Object> map = new HashMap<>();
    map.put("grant_type", getGrantType());
    return map;
  }
}
