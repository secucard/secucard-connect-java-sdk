package com.secucard.connect.model.smart;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.secucard.connect.model.MediaResource;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.general.Account;
import com.secucard.connect.model.loyalty.Customer;

import java.util.Date;

public class Checkin extends SecuObject {
  public static final String OBJECT = "smart.checkins";

  private String customerName;

  private String picture;

  @JsonIgnore
  private MediaResource pictureObject;

  private Date created;

  private Account account;

  private Customer customer;

  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public String getPicture() {
    return picture;
  }

  public void setPicture(String picture) {
    this.picture = picture;
    pictureObject = MediaResource.create(picture);
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public MediaResource getPictureObject() {
    return pictureObject;
  }

  @Override
  public String getObject() {
    return OBJECT;
  }


  @Override
  public String toString() {
    return "Checkin{" +
        "customerName='" + customerName + '\'' +
        ", pictureUrl='" + picture + '\'' +
        ", created=" + created +
        "} " + super.toString();
  }
}
