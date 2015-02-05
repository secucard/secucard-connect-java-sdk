package com.secucard.connect.model.payment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.annotation.ProductInfo;
import com.secucard.connect.model.general.merchant.Merchant;

import java.util.Date;

@ProductInfo(resourceId = "payment.containers")
public class Container extends SecuObject {
  @JsonIgnore
  public static final String TYPE_BANK_ACCOUNT = "bank_account";

  private Merchant merchant;

  @JsonProperty("private")
  private Data privateData;

  @JsonProperty("public")
  private Data publicData;

  @JsonProperty("assign")
  private Customer assigned;

  private String type;

  private Date created;

  private Date updated;

  public Merchant getMerchant() {
    return merchant;
  }

  public void setMerchant(Merchant merchant) {
    this.merchant = merchant;
  }

  public Data getPrivateData() {
    return privateData;
  }

  public void setPrivateData(Data privateData) {
    this.privateData = privateData;
  }

  public Data getPublicData() {
    return publicData;
  }

  public void setPublicData(Data publicData) {
    this.publicData = publicData;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
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

  public Customer getAssigned() {
    return assigned;
  }

  public void setAssigned(Customer assigned) {
    this.assigned = assigned;
  }

  @Override
  public String toString() {
    return "Container{" +
        "merchant=" + merchant +
        ", privateData=" + privateData +
        ", publicData=" + publicData +
        ", type='" + type + '\'' +
        ", created=" + created +
        ", updated=" + updated +
        '}';
  }
}
