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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.product.common.model.MediaResource;
import com.secucard.connect.product.common.model.SecuObject;
import com.secucard.connect.product.general.model.Merchant;

public class CardGroup extends SecuObject {

  public static final String TRANSACTION_TYPE_CHARGE = "charge";
  public static final String TRANSACTION_TYPE_DISCHARGE = "discharge";
  public static final String TRANSACTION_TYPE_SALE_REVENUE = "sale_revenue";
  public static final String TRANSACTION_TYPE_CHARGE_POINTS = "charge_points";
  public static final String TRANSACTION_TYPE_DISCHARGE_POINTS = "discharge_points";
  public static final String TRANSACTION_TYPE_CASHREPORT = "cashreport";

  @JsonProperty("display_name")
  private String displayName;

  @JsonProperty("display_name_raw")
  private String displayNameRaw;

  @JsonProperty("stock_warn_limit")
  private int stockWarnLimit;

  private Merchant merchant;

  private String picture;

  @JsonIgnore
  private MediaResource pictureObject;

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayNameRaw() {
    return displayNameRaw;
  }

  public void setDisplayNameRaw(String displayNameRaw) {
    this.displayNameRaw = displayNameRaw;
  }

  public int getStockWarnLimit() {
    return stockWarnLimit;
  }

  public void setStockWarnLimit(int stockWarnLimit) {
    this.stockWarnLimit = stockWarnLimit;
  }

  public Merchant getMerchant() {
    return merchant;
  }

  public void setMerchant(Merchant merchant) {
    this.merchant = merchant;
  }

  public MediaResource getPictureObject() {
    return pictureObject;
  }

  public String getPicture() {
    return picture;
  }

  public void setPicture(String value) {
    this.picture = value;
    pictureObject = MediaResource.create(picture);
  }
}
