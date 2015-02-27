package com.secucard.connect.model.general;

import com.secucard.connect.model.SecuObject;

public class AccountDevice extends SecuObject {
  public static final String OBJECT = "general.accountdevices";

  // todo: add proerties

  @Override
  public String getObject() {
    return OBJECT;
  }
}
