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

public class ClientCredentials extends OAuthCredentials {
  protected final String clientId;
  protected final String clientSecret;

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
  public String getId() {
    return getGrantType() + clientId + clientSecret;
  }

  @Override
  public Map<String, Object> asMap() {
    Map<String, Object> map = super.asMap();
    map.put("client_id", clientId);
    map.put("client_secret", clientSecret);
    return map;
  }
}
