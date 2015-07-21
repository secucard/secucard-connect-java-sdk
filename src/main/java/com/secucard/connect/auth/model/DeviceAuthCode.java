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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Container for authentication data requested during a OAuth device flow.
 */
public class DeviceAuthCode {
  @JsonProperty("device_code")
  private String deviceCode;

  @JsonProperty("user_code")
  private String userCode;

  @JsonProperty("verification_url")
  private String verificationUrl;

  @JsonProperty("expires_in")
  private int expiresIn;

  private int interval;

  public String getDeviceCode() {
    return deviceCode;
  }

  public void setDeviceCode(String deviceCode) {
    this.deviceCode = deviceCode;
  }

  public String getUserCode() {
    return userCode;
  }

  public void setUserCode(String userCode) {
    this.userCode = userCode;
  }

  public String getVerificationUrl() {
    return verificationUrl;
  }

  public void setVerificationUrl(String verificationUrl) {
    this.verificationUrl = verificationUrl;
  }

  public int getExpiresIn() {
    return expiresIn;
  }

  public void setExpiresIn(int expiresIn) {
    this.expiresIn = expiresIn;
  }

  public int getInterval() {
    return interval;
  }

  public void setInterval(int interval) {
    this.interval = interval;
  }

  @Override
  public String toString() {
    return "DeviceAuthCode{" +
        "deviceCode='" + deviceCode + '\'' +
        ", userCode='" + userCode + '\'' +
        ", verificationUrl='" + verificationUrl + '\'' +
        ", expiresIn=" + expiresIn +
        ", interval=" + interval +
        '}';
  }
}
