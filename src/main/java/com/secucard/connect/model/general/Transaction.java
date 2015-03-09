package com.secucard.connect.model.general;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.loyalty.Sale;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;

public class Transaction extends SecuObject {
  public static final String OBJECT = "general.transactions";

  public static final String TYPE_SALE = "sale";
  public static final String TYPE_CHARGE = "charge";

  private Merchant merchant;

  private int amount;

  @JsonProperty("last_change")
  private Date lastChange;

  private String type;

  private Sale details;

  private Currency currency;

  @Override
  public String getObject() {
    return OBJECT;
  }

  public Merchant getMerchant() {
    return merchant;
  }

  public void setMerchant(Merchant merchant) {
    this.merchant = merchant;
  }

  public int getAmount() {
    return amount;
  }

  public void setAmount(int amount) {
    this.amount = amount;
  }

  public Date getLastChange() {
    return lastChange;
  }

  public void setLastChange(Date lastChange) {
    this.lastChange = lastChange;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Sale getDetails() {
    return details;
  }

  public void setDetails(Sale details) {
    this.details = details;
  }

  public Currency getCurrency() {
    return currency;
  }

  public void setCurrency(Currency currency) {
    this.currency = currency;
  }
}
