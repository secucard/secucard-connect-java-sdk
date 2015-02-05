package com.secucard.connect.model.payment;

public class  Data {
  private String owner;

  private String iban;

  private String bic;

  public Data() {
  }

  public Data(String owner, String iban, String bic) {
    this.owner = owner;
    this.iban = iban;
    this.bic = bic;
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


  @Override
  public String toString() {
    return "Data{" +
        "owner='" + owner + '\'' +
        ", iban='" + iban + '\'' +
        ", bic='" + bic + '\'' +
        '}';
  }
}
