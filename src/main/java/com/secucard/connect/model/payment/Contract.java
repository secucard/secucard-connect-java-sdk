package com.secucard.connect.model.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.general.Merchant;

import java.util.Date;

public class Contract extends SecuObject {
  public static final String OBJECT = "payment.contracts";

  @JsonProperty("contract_id")
  private String contractId;

  private Merchant merchant;

  @JsonProperty("internal_reference")
  private String internalReference;

  private Date created;

  public Merchant getMerchant() {
    return merchant;
  }

  public void setMerchant(Merchant merchant) {
    this.merchant = merchant;
  }

  public String getInternalReference() {
    return internalReference;
  }

  public void setInternalReference(String internalReference) {
    this.internalReference = internalReference;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public String getContractId() {
    return contractId;
  }

  public void setContractId(String contractId) {
    this.contractId = contractId;
  }

  @Override
  public String getObject() {
    return OBJECT;
  }
}
