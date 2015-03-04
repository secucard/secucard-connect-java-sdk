package com.secucard.connect.model.loyalty;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Currency;

public class Condition {

  public static final String BONUS_TYPE_PERCENT = "percent";

  @JsonProperty("start_value")
  private BigDecimal startValue;

  /**
   * PTS or EUR
   */
  @JsonProperty("curreny")
  private String currency;

  private int bonus;

  @JsonProperty("bonus_type")
  private String bonusType;

  public BigDecimal getStartValue() {
    return startValue;
  }

  public void setStartValue(BigDecimal startValue) {
    this.startValue = startValue;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public int getBonus() {
    return bonus;
  }

  public void setBonus(int bonus) {
    this.bonus = bonus;
  }

  public String getBonusType() {
    return bonusType;
  }

  public void setBonusType(String bonusType) {
    this.bonusType = bonusType;
  }
}