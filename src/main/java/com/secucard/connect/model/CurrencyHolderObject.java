package com.secucard.connect.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.Currency;

public class CurrencyHolderObject extends SecuObject {
  private CurrencyHolder currencyHolder = new CurrencyHolder();

  public String getCurrency() {
    return currencyHolder.getCurrency();
  }

  public static String getValue(BigDecimal value) {
    return CurrencyHolder.getValue(value);
  }

  public static Currency getWIR() {
    return CurrencyHolder.getWIR();
  }

  @JsonIgnore
  public void setCurrency(Currency currency) {
    currencyHolder.setCurrency(currency);
  }

  public Currency getCurrencyAsCurrency() {
    return currencyHolder.getCurrencyAsCurrency();
  }

  public static Currency getGreatBritainPound() {
    return CurrencyHolder.getGreatBritainPound();
  }

  public static Currency getEuro() {
    return CurrencyHolder.getEuro();
  }

  public void setCurrency(String currency) {
    currencyHolder.setCurrency(currency);
  }

  public static BigDecimal getValue(String value) {
    return CurrencyHolder.getValue(value);
  }

  public static Currency getUSDollar() {
    return CurrencyHolder.getUSDollar();
  }

  public static Currency getSchweizerFranken() {
    return CurrencyHolder.getSchweizerFranken();
  }
}
