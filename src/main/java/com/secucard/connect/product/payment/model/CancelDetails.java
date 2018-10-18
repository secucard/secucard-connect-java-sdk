package com.secucard.connect.product.payment.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

/**
 * Holds Payment basket details.
 */
public class CancelDetails implements Serializable {

  @JsonProperty("new_trans_id")
  private int newTransId;

  @JsonProperty("remaining_amount")
  private int remainingAmount;

  @JsonProperty("refund_waiting_for_payment")
  private boolean refundWaitingForPayment;

  private boolean demo;

  public int getNewTransId() {
    return newTransId;
  }

  public void setNewTransId(int newTransId) {
    this.newTransId = newTransId;
  }

  public int getRemainingAmount() {
    return remainingAmount;
  }

  public void setRemainingAmount(int remainingAmount) {
    this.remainingAmount = remainingAmount;
  }

  public boolean isRefundWaitingForPayment() {
    return refundWaitingForPayment;
  }

  public void setRefundWaitingForPayment(boolean refundWaitingForPayment) {
    this.refundWaitingForPayment = refundWaitingForPayment;
  }

  public boolean isDemo() {
    return demo;
  }

  public void setDemo(boolean demo) {
    this.demo = demo;
  }

  @Override
  public String toString() {
    return "newTransId='" + newTransId + "\', " +
           "remainingAmount='" + remainingAmount + "\', " +
           "refundWaitingForPayment='" + refundWaitingForPayment + "\', " +
           "demo='" + demo + "\'";
  }

}
