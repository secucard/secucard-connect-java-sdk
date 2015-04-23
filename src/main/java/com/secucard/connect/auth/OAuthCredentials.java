package com.secucard.connect.auth;

import java.util.HashMap;
import java.util.Map;

public abstract class OAuthCredentials {

  public abstract String getGrantType();

  /**
   * Returns an id which uniquely identifies this instance in a way that same ids refer to the same credentials.
   *
   * @return The id as string.
   */
  public abstract String getId();

  public Map<String, Object> asMap() {
    HashMap<String, Object> map = new HashMap<>();
    map.put("grant_type", getGrantType());
    return map;
  }
}
