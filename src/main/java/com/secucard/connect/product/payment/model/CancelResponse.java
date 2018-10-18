package com.secucard.connect.product.payment.model;

import java.io.Serializable;

public class CancelResponse implements Serializable {

  private CancelDetails result;

  public CancelDetails getResult() {
    return result;
  }

  public void setResult(CancelDetails result) {
    this.result = result;
  }

  public String toString() {
    return "result{" + result + '}';
  }

}
