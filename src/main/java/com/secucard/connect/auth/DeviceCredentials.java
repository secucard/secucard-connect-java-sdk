package com.secucard.connect.auth;

import java.util.Map;

public class DeviceCredentials extends ClientCredentials {
  private String deviceCode;

  public DeviceCredentials(String clientId, String clientSecret, String deviceId) {
    super(clientId, clientSecret);
    this.deviceId = deviceId;
  }

  public void setDeviceCode(String deviceCode) {
    this.deviceCode = deviceCode;
  }

  @Override
  public String getGrantType() {
    return "device";
  }

  @Override
  public Map<String, Object> asMap() {
    Map<String, Object> map = super.asMap();
    if (deviceId != null) {
      map.put("uuid", deviceId);
    }
    if (deviceCode != null) {
      map.put("code", deviceCode);
    }
    return map;
  }
}
