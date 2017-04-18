package com.secucard.connect.product.payment.model;

public class SecupayDebit extends Transaction {

  private Container container;

  public Container getContainer() {
    return container;
  }

  public void setContainer(Container container) {
    this.container = container;
  }

  @Override
  public String toString() {
    return "SecupayDebit{" + "container=" + container + "} " + super.toString();
  }
}
