package com.secucard.connect.model.loyalty;

import java.io.Serializable;

public class Bonus implements Serializable {


  private int amount;

  /**
   * PTS or EUR
   */
  private String currency;

  private int balance;

  public int getAmount() {
    return amount;
  }

  public void setAmount(int amount) {
    this.amount = amount;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public int getBalance() {
    return balance;
  }

  public void setBalance(int balance) {
    this.balance = balance;
  }
}
