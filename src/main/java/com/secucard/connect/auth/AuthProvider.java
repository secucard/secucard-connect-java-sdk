package com.secucard.connect.auth;

import com.secucard.connect.java.client.oauth.OAuthToken;

public interface AuthProvider {
  OAuthToken getToken();
}
