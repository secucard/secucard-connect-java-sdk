package com.secucard.connect.product.loyalty.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.product.common.model.SecuObject;
import com.secucard.connect.product.general.model.Account;

public class Card extends SecuObject {
  @JsonProperty("cardnumber")
  private String cardNumber;

  @JsonProperty
  private String created;

  @JsonProperty
  private Account account;

  public String getCardNumber() {
    return cardNumber;
  }

  public void setCardNumber(String cardNumber) {
    this.cardNumber = cardNumber;
  }

  public String getCreated() {
    return created;
  }

  public void setCreated(String created) {
    this.created = created;
  }

  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }
}
