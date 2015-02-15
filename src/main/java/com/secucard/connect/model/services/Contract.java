package com.secucard.connect.model.services;

import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.annotation.ProductInfo;

public class Contract extends SecuObject {
  public static final String OBJECT = "services.contracts";

  @Override
  public String getObject() {
    return OBJECT;
  }
}
