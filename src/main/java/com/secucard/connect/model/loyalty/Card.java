package com.secucard.connect.model.loyalty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.general.Account;

public class Card extends SecuObject {
  public static final String OBJECT = "loyalty.cards";

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

  @Override
  public String getObject() {
    return OBJECT;
  }
}
