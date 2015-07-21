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

  public AppUserCredentials(ClientCredentials clientCredentials, String userName, String password, String deviceId) {
    this(clientCredentials.getClientId(), clientCredentials.getClientSecret(), userName, password, deviceId);
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
