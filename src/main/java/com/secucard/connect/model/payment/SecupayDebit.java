package com.secucard.connect.model.payment;

public class SecupayDebit extends Transaction {
  public static final String OBJECT = "payment.secupaydebits";

  private Container container;

  public Container getContainer() {
    return container;
  }

  public void setContainer(Container container) {
    this.container = container;
  }


  @Override
  public String toString() {
    return "SecupayDebit{" +
        "container=" + container +
        "} " + super.toString();
  }
}
