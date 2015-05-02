package com.secucard.connect.auth;

public class AnonymousCredentials extends OAuthCredentials {
  @Override
  public String getGrantType() {
    return "anonymous";
  }

  @Override
  public String getId() {
    return "anonymous";
  }
}
