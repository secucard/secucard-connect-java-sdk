package com.secucard.connect.model.general.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.model.CurrencyHolderObject;
import com.secucard.connect.model.general.merchant.Merchant;
import com.secucard.connect.model.loyalty.Sale;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Steffen on 26.08.2014.
 */
public class Transaction extends CurrencyHolderObject {
  public static final String OBJECT = "general.transactions";

  public static final String TYPE_SALE = "sale";
  public static final String TYPE_CHARGE = "charge";

  private Merchant merchant;

  private BigDecimal amount;

  @JsonProperty("last_change")
  private Date lastChange;

  private String type;

  private Sale details;

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

  public String getAmount() {
    return getValue(amount);
  }

  public BigDecimal getAmountAsBigDecimal() {
    return amount;
  }

  public void setAmount(String amount) {
    this.amount = getValue(amount);
  }

  @JsonIgnore
  public void setAmount(BigDecimal amount) {
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
}
