package com.secucard.connect.auth.model;

import java.util.Map;

public class DeviceCredentials extends ClientCredentials {
  /**
   * Code obtained during the authorisation process
   */
  private String deviceCode;


  private String id;

  /**
   * A unique device id like UUID.
   */
  protected String deviceId;

  public DeviceCredentials(String clientId, String clientSecret, String deviceId) {
    super(clientId, clientSecret);
    this.deviceId = deviceId;
    this.id = getGrantType() + clientId + clientSecret + deviceId;
  }

  public String getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(String deviceId) {
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
  public String getId() {
    return id;
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
