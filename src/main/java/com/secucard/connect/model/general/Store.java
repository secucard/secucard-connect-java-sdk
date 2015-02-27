package com.secucard.connect.model.general;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.model.SecuObject;

public class Store extends SecuObject {
  public static final String OBJECT = "general.stores";

  @JsonProperty
  private String name;

  @JsonProperty("name_raw")
  private String nameRaw;

  @JsonProperty
  private Merchant merchant;

  @Override
  public String getObject() {
    return OBJECT;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getNameRaw() {
    return nameRaw;
  }

  public void setNameRaw(String nameRaw) {
    this.nameRaw = nameRaw;
  }

  public Merchant getMerchant() {
    return merchant;
  }

  public void setMerchant(Merchant merchant) {
    this.merchant = merchant;
  }
}
