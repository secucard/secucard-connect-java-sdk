package com.secucard.connect.model.services;

import com.secucard.connect.model.SecuObject;

public class Contract extends SecuObject {
  public static final String OBJECT = "services.contracts";

  @Override
  public String getObject() {
    return OBJECT;
  }
}
