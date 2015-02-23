package com.secucard.connect.model.general.accounts;

import com.secucard.connect.model.SecuObject;

public class Account extends SecuObject {
  public static final String OBJECT = "general.accounts";

  @Override
  public String getObject() {
    return OBJECT;
  }
}
