/*
 * Copyright (c) 2015. hp.weber GmbH & Co secucard KG (www.secucard.com)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

  public DeviceCredentials(ClientCredentials clientCredentials, String deviceId) {
    this(clientCredentials.getClientId(), clientCredentials.getClientSecret(), deviceId);
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
