package com.secucard.connect.model.smart;

import java.util.Currency;

public class BasketInfo {

  private float sum;

  private String currency; // todo: use Currency  constante

  public BasketInfo() {
  }

  public BasketInfo(float sum, Currency currency) {
    this.sum = sum;
    this.currency = currency.getCurrencyCode();
  }

  public float getSum() {
    return sum;
  }

  public void setSum(float sum) {
    this.sum = sum;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  @Override
  public String toString() {
    return "BasketInfo{" +
        "sum=" + sum +
        ", currency='" + currency + '\'' +
        '}';
  }

  public static Currency getEuro() {
    return Currency.getInstance("EUR");
  }

  public static Currency getUSDollar() {
    return Currency.getInstance("USD");
  }
}
