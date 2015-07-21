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
import com.secucard.connect.product.common.model.SecuObject;

import java.io.Serializable;
import java.util.List;

public class Program extends SecuObject {

  private String description;

  @JsonProperty("cardgroup")
  private CardGroup cardGroup;

  private List<Condition> conditions;

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public CardGroup getCardGroup() {
    return cardGroup;
  }

  public void setCardGroup(CardGroup cardGroup) {
    this.cardGroup = cardGroup;
  }

  public List<Condition> getConditions() {
    return conditions;
  }

  public void setConditions(List<Condition> conditions) {
    this.conditions = conditions;
  }

  public static class Condition implements Serializable {
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
}
