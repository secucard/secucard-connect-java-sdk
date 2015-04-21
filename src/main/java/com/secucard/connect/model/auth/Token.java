package com.secucard.connect.model.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Token implements Serializable {

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

    public Token() {
    }

    public Token(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public Long getOrigExpireTime() {
      return origExpireTime;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime() {
        this.expireTime = System.currentTimeMillis() + expiresIn * 1000;
        if (origExpireTime == null){
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
        '}';
  }
}
