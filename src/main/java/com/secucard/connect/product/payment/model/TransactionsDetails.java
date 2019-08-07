package com.secucard.connect.product.payment.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.product.common.model.SecuObject;
import com.secucard.connect.product.general.model.Contact;
import com.secucard.connect.product.general.model.Merchant;
import java.util.Currency;
import java.util.Date;

public class TransactionsDetails {

  protected String cleared;

  protected int status;

  @JsonProperty("status_text")
  protected String statusText;

  @JsonProperty("status_simple")
  protected int statusSimple;

  @JsonProperty("status_simple_text")
  protected String statusSimpleText;

  protected long amount;

  protected String description;

  @JsonProperty("description_raw")
  protected String descriptionRaw;

  public String getCleared() {
    return cleared;
  }

  public void setCleared(String cleared) {
    this.cleared = cleared;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getStatusText() {
    return statusText;
  }

  public void setStatusText(String statusText) {
    this.statusText = statusText;
  }

  public int getStatusSimple() {
    return statusSimple;
  }

  public void setStatusSimple(int statusSimple) {
    this.statusSimple = statusSimple;
  }

  public String getStatusSimpleText() {
    return statusSimpleText;
  }

  public void setStatusSimpleText(String statusSimpleText) {
    this.statusSimpleText = statusSimpleText;
  }

  public long getAmount() {
    return amount;
  }

  public void setAmount(long amount) {
    this.amount = amount;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDescriptionRaw() {
    return descriptionRaw;
  }

  public void setDescriptionRaw(String descriptionRaw) {
    this.descriptionRaw = descriptionRaw;
  }

  @Override
  public String toString() {
    return "TransactionsDetails{"
        + "cleared='" + getCleared() + '\''
        + ", status='" + getStatus() + '\''
        + ", statusText='" + getStatusText() + '\''
        + ", statusSimple='" + getStatusSimple() + '\''
        + ", statusSimpleText='" + getStatusSimpleText() + '\''
        + ", amount='" + getAmount() + '\''
        + ", description='" + getDescription() + '\''
        + ", descriptionRaw='" + getDescriptionRaw() + '\''
        + '}';
  }
}
