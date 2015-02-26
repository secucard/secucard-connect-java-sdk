package com.secucard.connect.model.payment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.model.CurrencyHolderObject;

import java.math.BigDecimal;

public class SecupayDebit extends CurrencyHolderObject {
  private Container container;

  private Customer customer;

  private Contract contract;

  private BigDecimal amount;

  private String purpose;

  @JsonProperty("order_id")
  private String orderId;

  @JsonProperty("trans_id")
  private String transId;

  private String status;

  public Container getContainer() {
    return container;
  }

  public void setContainer(Container container) {
    this.container = container;
  }

  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  public Contract getContract() {
    return contract;
  }

  public void setContract(Contract contract) {
    this.contract = contract;
  }

  public String getTransId() {
    return transId;
  }

  public void setTransId(String transId) {
    this.transId = transId;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getAmount() {
    return getValue(amount);
  }

  public BigDecimal getAmountAsBigDecimal() {
    return amount;
  }

  @JsonIgnore
  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public void setAmount(String amount) {
    this.amount = getValue(amount);
  }

  public String getPurpose() {
    return purpose;
  }

  public void setPurpose(String purpose) {
    this.purpose = purpose;
  }

  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

  @Override
  public String toString() {
    return "SecupayDebit{" +
        "container='" + container + '\'' +
        ", customer='" + customer + '\'' +
        ", contract='" + contract + '\'' +
        ", amount=" + amount +
        ", purpose='" + purpose + '\'' +
        ", orderId='" + orderId + '\'' +
        "} " + super.toString();
  }
}
