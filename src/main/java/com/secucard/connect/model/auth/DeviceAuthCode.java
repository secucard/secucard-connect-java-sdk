package com.secucard.connect.model.auth;

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
