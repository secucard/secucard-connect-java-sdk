package com.secucard.connect.product.payment.model;

/**
 * Holds payment container details.
 */
public class Data {

  private String owner;

  private String iban;

  private String bic;

  private String bankname;

  public Data() {
  }

  public Data(String iban) {
    this.iban = iban;
  }

  public Data(String iban, String owner) {
    this.iban = iban;
    this.owner = owner;
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
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

  public String getBankname() {
    return bankname;
  }

  public void setBankname(String bankname) {
    this.bankname = bankname;
  }

  @Override
  public String toString() {
    return "Data{" + "owner='" + owner + '\'' + ", iban='" + iban + '\'' + ", bic='" + bic + '\''
        + ", bankname='" + bankname + '\'' + '}';
  }
}
