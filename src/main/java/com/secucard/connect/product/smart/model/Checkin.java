/*
 * Copyright (c) 2015. hp.weber GmbH & Co secucard KG (www.secucard.com)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.secucard.connect.product.smart.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.secucard.connect.product.common.model.MediaResource;
import com.secucard.connect.product.common.model.SecuObject;
import com.secucard.connect.product.general.model.Account;
import com.secucard.connect.product.loyalty.model.Customer;

import java.util.Date;

/**
 * The Check-In data.
 * The error field may be not null if an error happened during retrieval of the picture object.
 */
public class Checkin extends SecuObject {

  private String customerName;

  private String picture;

  @JsonIgnore
  private MediaResource pictureObject;

  private Date created;

  private Account account;

  private Customer customer;

  @JsonIgnore
  private Exception error;

  public Exception getError() {
    return error;
  }

  public void setError(Exception error) {
    this.error = error;
  }

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

  /**
   * Returns the customer picture. Picture is not available if null - if the error field is not null this was caused
   * by an error.
   */
  public MediaResource getPictureObject() {
    return pictureObject;
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
