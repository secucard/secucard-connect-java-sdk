package com.secucard.connect.model.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.general.Merchant;

import java.util.Date;

public class Contract extends SecuObject {
  public static final String OBJECT = "payment.contracts";

  private Merchant merchant;

  private Date created;

  private Date updated;

  private Contract parent;

  @JsonProperty("allow_cloning")
  private boolean allowCloning;

  public Merchant getMerchant() {
    return merchant;
  }

  public void setMerchant(Merchant merchant) {
    this.merchant = merchant;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public Date getUpdated() {
    return updated;
  }

  public void setUpdated(Date updated) {
    this.updated = updated;
  }

  public Contract getParent() {
    return parent;
  }

  public void setParent(Contract parent) {
    this.parent = parent;
  }

  public boolean isAllowCloning() {
    return allowCloning;
  }

  public void setAllowCloning(boolean allowCloning) {
    this.allowCloning = allowCloning;
  }

  @Override
  public String getObject() {
    return OBJECT;
  }

  @Override
  public String toString() {
    return "Contract{" +
        "merchant=" + merchant +
        ", created=" + created +
        ", updated=" + updated +
        ", parent=" + parent +
        ", allowCloning=" + allowCloning +
        "} " + super.toString();
  }
}
