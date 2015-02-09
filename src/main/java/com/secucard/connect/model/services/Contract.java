package com.secucard.connect.model.services;

import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.annotation.ProductInfo;

@ProductInfo(resourceId = "services.contracts")
public class Contract extends SecuObject {
  @Override
  public String getObject() {
    return "services.contracts";
  }
}
