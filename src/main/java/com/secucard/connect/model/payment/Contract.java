package com.secucard.connect.model.payment;

import com.secucard.connect.model.SecuObject;

public class Contract extends SecuObject {

  public static final String OBJECT = "payment.contracts";

  @Override
  public String getObject() {
    return OBJECT;
  }
}
