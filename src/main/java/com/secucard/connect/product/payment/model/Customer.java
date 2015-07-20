package com.secucard.connect.product.payment.model;

import com.secucard.connect.product.common.model.SecuObject;
import com.secucard.connect.product.general.model.Contact;
import com.secucard.connect.product.general.model.Merchant;

import java.util.Date;

public class Customer extends SecuObject {
  private Merchant merchant;

  private Contact contact;

  private Date created;

  private Date updated;

  private Contract contract;


  public Contract getContract() {
    return contract;
  }

  public void setContract(Contract contract) {
    this.contract = contract;
  }

  public Merchant getMerchant() {
    return merchant;
  }

  public void setMerchant(Merchant merchant) {
    this.merchant = merchant;
  }

  public Contact getContact() {
    return contact;
  }

  public void setContact(Contact contact) {
    this.contact = contact;
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

  @Override
  public String toString() {
    return "Customer{" +
        "merchant=" + merchant +
        ", contact=" + contact +
        ", created=" + created +
        ", updated=" + updated +
        ", contract=" + contract +
        "} " + super.toString();
  }
}