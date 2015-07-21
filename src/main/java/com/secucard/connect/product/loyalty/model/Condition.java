/*
 * Copyright (c) 2015. hp.weber GmbH & Co secucard KG (www.secucard.com)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.secucard.connect.product.loyalty.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Condition implements Serializable {

  public static final String BONUS_TYPE_PERCENT = "percent";

  @JsonProperty("start_value")
  private int startValue;

  /**
   * PTS or EUR
   */
  @JsonProperty("curreny")
  private String currency;

  private int bonus;

  @JsonProperty("bonus_type")
  private String bonusType;

  public int getStartValue() {
    return startValue;
  }

  public void setStartValue(int startValue) {
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
