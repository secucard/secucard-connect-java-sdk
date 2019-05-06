package com.secucard.connect.product.payment.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.product.common.model.SecuObject;
import java.util.Arrays;
import java.util.Currency;

public class SecupayPayout extends SecuObject {

  protected String customer;

  @JsonProperty("redirect_url")
  protected RedirectUrl redirectUrl;

  @JsonProperty("opt_data")
  protected OptData optData;

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

  protected boolean demo;

  @JsonProperty("transfer_purpose")
  private String transferPurpose;

  @JsonProperty("transfer_account")
  private TransferAccount transferAccount;

  @JsonProperty("transaction_list")
  private TransactionList[] transactionList;


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

  public RedirectUrl getRedirectUrl() {
    return redirectUrl;
  }

  public void setRedirectUrl(RedirectUrl redirectUrl) {
    this.redirectUrl = redirectUrl;
  }

  public OptData getOptData() {
    return optData;
  }

  public void setOptData(OptData optData) {
    this.optData = optData;
  }

  public String getTransferPurpose() {
    return transferPurpose;
  }

  public void setTransferPurpose(String transferPurpose) {
    this.transferPurpose = transferPurpose;
  }

  public boolean getDemo() {
    return demo;
  }

  public void setDemo(boolean demo) {
    this.demo = demo;
  }

  public TransferAccount getTransferAccount() {
    return transferAccount;
  }

  public void setTransferAccount(TransferAccount transferAccount) {
    this.transferAccount = transferAccount;
  }

  public TransactionList[] getTransactionList() {
    return transactionList;
  }

  public void setTransactionList(TransactionList[] transactionList) {
    this.transactionList = transactionList;
  }

  public String getCustomer() {
    return customer;
  }

  public void setCustomer(String customerId) {
    this.customer = customerId;
  }

  @Override
  public String toString() {
    return "SecupayPayout{" + "amount='" + getAmount() + '\'' + ", currency='" + getCurrency() + '\'' + ", currency='" + getCurrency() + '\''
        + ", demo='" + getDemo() + '\'' + ", optData='" + getOptData() + '\'' + ", orderId='" + getOrderId() + '\'' + ", purpose='" + getPurpose()
        + '\'' + ", redirectUrl='" + getRedirectUrl() + '\'' + ", status='" + getStatus() + '\'' + ", transactionList='" + Arrays
        .toString(getTransactionList()) + '\'' + ", transactionStatus='" + getTransactionStatus() + '\'' + ", transferAccount='"
        + getTransferAccount() + '\'' + ", transferPurpose='" + getTransferPurpose() + '\'' + ", transId='" + getTransId() + '\'' + ", " + super
        .toString() + '}';
  }

}
