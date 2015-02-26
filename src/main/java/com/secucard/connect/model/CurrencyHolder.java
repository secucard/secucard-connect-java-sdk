package com.secucard.connect.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.Currency;

public class CurrencyHolder {
  private Currency currency;

  public String getCurrency() {
    return currency.getCurrencyCode();
  }

  public Currency getCurrencyAsCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = Currency.getInstance(currency);
  }

  @JsonIgnore
  public void setCurrency(Currency currency) {
    this.currency = currency;
  }

  protected static String getValue(BigDecimal value) {
    return value == null ? null : value.toString();
  }

  protected static BigDecimal getValue(String value) {
    return value == null ? null : new BigDecimal(value);
  }

  public static Currency getEuro() {
    return Currency.getInstance("EUR");
  }

  public static Currency getUSDollar() {
    return Currency.getInstance("USD");
  }

  public static Currency getSchweizerFranken() {
    return Currency.getInstance("CHF");
  }

  public static Currency getGreatBritainPound() {
    return Currency.getInstance("GBP");
  }

  public static Currency getWIR() {
    return Currency.getInstance("CHW");
  }
}
