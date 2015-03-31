package com.secucard.connect.model.payment;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransferAccount {

  @JsonProperty("account_owner")
  private String accountOwner;

  @JsonProperty("accountnumber")
  private String accountNumber;

  private String iban;

  private String bic;

  @JsonProperty("bankcode")
  private String bankCode;

  public String getAccountOwner() {
    return accountOwner;
  }

  public void setAccountOwner(String accountOwner) {
    this.accountOwner = accountOwner;
  }

  public String getAccountNumber() {
    return accountNumber;
  }

  public void setAccountNumber(String accountNumber) {
    this.accountNumber = accountNumber;
  }

  public String getIban() {
    return iban;
  }

  public void setIban(String iban) {
    this.iban = iban;
  }

  public String getBic() {
    return bic;
  }

  public void setBic(String bic) {
    this.bic = bic;
  }

  public String getBankCode() {
    return bankCode;
  }

  public void setBankCode(String bankCode) {
    this.bankCode = bankCode;
  }

  @Override
  public String toString() {
    return "TransferAccount{" +
        "accountOwner='" + accountOwner + '\'' +
        ", accountNumber='" + accountNumber + '\'' +
        ", iban='" + iban + '\'' +
        ", bic='" + bic + '\'' +
        ", bankCode='" + bankCode + '\'' +
        '}';
  }
}
