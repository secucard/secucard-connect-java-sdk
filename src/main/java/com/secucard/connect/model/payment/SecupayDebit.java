package com.secucard.connect.model.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.model.SecuObject;

import java.math.BigDecimal;
import java.util.Currency;

public class SecupayDebit extends SecuObject {
  public static final String OBJECT = "payment.secupaydebits";

  private Container container;

  private Customer customer;

  private Contract contract;

  private BigDecimal amount;

  private Currency currency;

  private String purpose;

  @JsonProperty("order_id")
  private String orderId;

  @JsonProperty("trans_id")
  private String transId;

  private String status;

  @Override
  public String getObject() {
    return OBJECT;
  }

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

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public Currency getCurrency() {
    return currency;
  }

  public void setCurrency(Currency currency) {
    this.currency = currency;
  }

  @Override
  public String toString() {
    return "SecupayDebit{" +
        "container=" + container +
        ", customer=" + customer +
        ", contract=" + contract +
        ", amount=" + amount +
        ", currency=" + currency +
        ", purpose='" + purpose + '\'' +
        ", orderId='" + orderId + '\'' +
        ", transId='" + transId + '\'' +
        ", status='" + status + '\'' +
        "} " + super.toString();
  }
}
