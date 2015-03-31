package com.secucard.connect.model.payment;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SecupayPrepay extends Transaction {
  public static final String OBJECT = "payment.secupayprepays";

  @JsonProperty("transfer_purpose")
  private String transferPurpose;

  @JsonProperty("transfer_account")
  private TransferAccount transferAccount;


  @Override
  public String getObject() {
    return OBJECT;
  }

  public String getTransferPurpose() {
    return transferPurpose;
  }

  public void setTransferPurpose(String transferPurpose) {
    this.transferPurpose = transferPurpose;
  }

  public TransferAccount getTransferAccount() {
    return transferAccount;
  }

  public void setTransferAccount(TransferAccount transferAccount) {
    this.transferAccount = transferAccount;
  }

  @Override
  public String toString() {
    return "SecupayPrepay{" +
        "transferPurpose='" + transferPurpose + '\'' +
        ", transactionStatus='" + transactionStatus + '\'' +
        ", transferAccount=" + transferAccount +
        "} " + super.toString();
  }
}
