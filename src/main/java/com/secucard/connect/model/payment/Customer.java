package com.secucard.connect.model.payment;

import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.general.Contact;
import com.secucard.connect.model.general.Merchant;

import java.util.Date;

public class Customer extends SecuObject {
  public static final String OBJECT = "payment.customers";

  private Merchant merchant;

  private Contact contact;

  private Date created;

  private Date updated;

  private Contract contract;

  @Override
  public String getObject() {
    return OBJECT;
  }

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
