package com.secucard.connect.product.payment.model;

public class SecupayCreditcard extends Transaction {

  private Container container;

  public Container getContainer() {
    return container;
  }

  public void setContainer(Container container) {
    this.container = container;
  }

  @Override
  public String toString() {
    return "SecupayCreditcard{" + "container=" + container + ", " + super.toString() + '}';
  }
}
