package com.secucard.connect.model.smart;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.model.transport.Status;

import java.util.List;

public class TransactionResult extends Status {
  private Transaction transaction;

  @JsonProperty("payment_method")
  private String paymentMethod;

  @JsonProperty("receipt")
  private List<ReceiptLine> receiptLines;

  public List<ReceiptLine> getReceiptLines() {
    return receiptLines;
  }

  public void setReceiptLines(List<ReceiptLine> receiptLines) {
    this.receiptLines = receiptLines;
  }

  public Transaction getTransaction() {
    return transaction;
  }

  public void setTransaction(Transaction transaction) {
    this.transaction = transaction;
  }


  public String getPaymentMethod() {
    return paymentMethod;
  }

  public void setPaymentMethod(String paymentMethod) {
    this.paymentMethod = paymentMethod;
  }

  @Override
  public String toString() {
    return "Result{" +
        "transaction=" + transaction +
        ", status='" + getStatus() + '\'' +
        ", error='" + getError() + '\'' +
        ", paymentMethod='" + paymentMethod + '\'' +
        ", receiptLines=" + receiptLines +
        '}';
  }
}
