package com.secucard.connect.auth.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Token implements Serializable {
  private static final long serialVersionUID = -5667703098179982024L;

  @JsonProperty("access_token")
  private String accessToken;

  @JsonProperty("expires_in")
  private int expiresIn;

  @JsonProperty("token_type")
  private String tokenType;

  private String scope;

  @JsonProperty("refresh_token")
  private String refreshToken;

  // UNIX timestamp of token expiring
  private Long expireTime;

  // the original expire time of token when token was created, set only one time, never changes
  private Long origExpireTime;

  // a unique id of this token
  private String id;

  public Long getOrigExpireTime() {
    return origExpireTime;
  }

  public Long getExpireTime() {
    return expireTime;
  }

  public void setExpireTime() {
    this.expireTime = System.currentTimeMillis() + expiresIn * 1000;
    if (origExpireTime == null) {
      origExpireTime = expireTime;
    }
  }

  @JsonIgnore
  public boolean isExpired() {
    return expireTime == null || System.currentTimeMillis() > expireTime;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public int getExpiresIn() {
    return expiresIn;
  }

  public void setExpiresIn(int expiresIn) {
    this.expiresIn = expiresIn;
  }

  public String getTokenType() {
    return tokenType;
  }

  public void setTokenType(String tokenType) {
    this.tokenType = tokenType;
  }

  public String getScope() {
    return scope;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "Token{" +
        "accessToken='" + accessToken + '\'' +
        ", expiresIn=" + expiresIn +
        ", tokenType='" + tokenType + '\'' +
        ", scope='" + scope + '\'' +
        ", refreshToken='" + refreshToken + '\'' +
        ", expireTime=" + expireTime +
        ", origExpireTime=" + origExpireTime +
        ", id='" + id + '\'' +
        '}';
  }
}
