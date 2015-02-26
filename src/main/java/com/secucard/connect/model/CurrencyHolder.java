package com.secucard.connect.model;

import java.math.BigDecimal;
import java.util.Currency;

public class CurrencyHolder {
  protected Currency currency;

  public CurrencyHolder() {
  }

  public CurrencyHolder(Currency currency) {
    this.currency = currency;
  }

  public String getCurrency() {
    return currency.getCurrencyCode();
  }

  public void setCurrency(String currency) {
    this.currency = Currency.getInstance(currency);
  }

  public void setCurrencyInstance(Currency currency) {
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
