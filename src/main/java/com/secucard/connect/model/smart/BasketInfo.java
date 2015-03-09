package com.secucard.connect.model.smart;

import java.math.BigDecimal;
import java.util.Currency;

public class BasketInfo {
  private int sum;

  private Currency currency;

  public BasketInfo() {
  }

  public BasketInfo(int sum, Currency currency) {
    this.sum = sum;
    this.currency = currency;
  }

  public BasketInfo(int sum, String currencyCode) {
    this.sum = sum;
    this.currency = Currency.getInstance(currencyCode);
  }

  public int getSum() {
    return sum;
  }

  public void setSum(int sum) {
    this.sum = sum;
  }

  public Currency getCurrency() {
    return currency;
  }

  public void setCurrency(Currency currency) {
    this.currency = currency;
  }

  @Override
  public String toString() {
    return "BasketInfo{" +
        "sum=" + sum +
        ", currency=" + currency +
        '}';
  }
}
