package com.secucard.connect.model.smart;

import java.math.BigDecimal;
import java.util.Currency;

public class BasketInfo {
  private BigDecimal sum;

  private Currency currency;

  public BasketInfo() {
  }

  public BasketInfo(BigDecimal sum, Currency currency) {
    this.sum = sum;
    this.currency = currency;
  }

  public BasketInfo(String sum, String currencyCode) {
    this.sum = new BigDecimal(sum);
    this.currency = Currency.getInstance(currencyCode);
  }

  public BigDecimal getSum() {
    return sum;
  }

  public void setSum(BigDecimal sum) {
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
