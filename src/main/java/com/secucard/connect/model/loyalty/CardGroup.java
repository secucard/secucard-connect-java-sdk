package com.secucard.connect.model.loyalty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.model.MediaResource;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.general.Merchant;

public class CardGroup extends SecuObject {
  public static final String OBJECT = "loyalty.cardgroups";

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
