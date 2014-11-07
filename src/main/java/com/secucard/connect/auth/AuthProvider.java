package com.secucard.connect.auth;


import com.secucard.connect.model.auth.Token;

public interface AuthProvider {
  Token getToken();
}
