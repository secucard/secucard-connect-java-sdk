package com.secucard.connect.model.auth;

import com.secucard.connect.model.SecuObject;

public class Session extends SecuObject {
  public static final String OBJECT = "auth.sessions";

  @Override
  public String getObject() {
    return OBJECT;
  }
}
