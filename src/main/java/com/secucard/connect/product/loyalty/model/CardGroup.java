package com.secucard.connect.product.loyalty.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.product.common.model.MediaResource;
import com.secucard.connect.product.common.model.SecuObject;
import com.secucard.connect.product.general.model.Merchant;

public class CardGroup extends SecuObject {
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
