package com.secucard.connect.model.smart;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.secucard.connect.model.CurrencyHolder;

import java.math.BigDecimal;
import java.util.Currency;

public class BasketInfo extends CurrencyHolder {
  private BigDecimal sum;

  public BasketInfo() {
  }

  public BasketInfo(BigDecimal sum, Currency currency) {
    this.sum = sum;
    setCurrencyInstance(currency);
  }

  public BasketInfo(String sum, Currency currency) {
    this(getValue(sum), currency);
  }

  public String getSum() {
    return getValue(sum);
  }

  public BigDecimal getSumAsBigDecimal() {
    return sum;
  }

  @JsonIgnore
  public void setSum(BigDecimal sum) {
    this.sum = sum;
  }

  public void setSum(String sum) {
    this.sum = getValue(sum);
  }

  @Override
  public String toString() {
    return "BasketInfo{" +
        "sum=" + sum +
        ", currency='" + currency + '\'' +
        '}';
  }
}
