package com.secucard.connect.model.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.model.SecuObject;

import java.util.Currency;

public abstract class Transaction extends SecuObject {
  public static final String STATUS_ACCEPTED = "accepted";
  public static final String STATUS_CANCELED = "canceled";
  public static final String STATUS_PROCEED = "proceed";

  protected Customer customer;

  protected Contract contract;

  protected long amount;

  protected Currency currency;

  protected String purpose;

  @JsonProperty("order_id")
  protected String orderId;

  @JsonProperty("trans_id")
  protected String transId;

  protected String status;

  @JsonProperty("transaction_status")
  protected String transactionStatus;

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

  public long getAmount() {
    return amount;
  }

  public void setAmount(long amount) {
    this.amount = amount;
  }

  public Currency getCurrency() {
    return currency;
  }

  public void setCurrency(Currency currency) {
    this.currency = currency;
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

  public String getTransactionStatus() {
    return transactionStatus;
  }

  public void setTransactionStatus(String transactionStatus) {
    this.transactionStatus = transactionStatus;
  }

}
