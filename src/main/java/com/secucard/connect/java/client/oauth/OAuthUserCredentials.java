package com.secucard.connect.java.client.oauth;

import javax.ws.rs.core.MultivaluedMap;

public interface OAuthUserCredentials {
  public String getType();

  public void addParameters(MultivaluedMap<String, String> map);
}
