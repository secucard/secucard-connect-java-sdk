package com.secucard.connect.model.loyalty.cardgroups;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.general.merchant.Merchant;

/**
 * Created by Steffen Schröder on 25.02.15.
 */
public class CardGroup extends SecuObject {
  public static final String OBJECT = "loyalty.cardgroups";

  @JsonProperty("display_name")
  private String displayName;

  @JsonProperty("display_name_raw")
  private String displayNameRaw;

  @JsonProperty("stock_warn_limit")
  private int stockWarnLimit;

  @JsonProperty
  private Merchant merchant;

  @Override
  public String getObject() {
    return OBJECT;
  }

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
}
