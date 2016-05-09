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

package com.secucard.connect.product.payment.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.product.common.model.SecuObject;
import com.secucard.connect.product.general.model.Merchant;

import java.util.Date;

public class Container extends SecuObject {
  @JsonIgnore
  public static final String TYPE_BANK_ACCOUNT = "bank_account";

  private Merchant merchant;

  @JsonProperty("private")
  private Data privateData;

  @JsonProperty("public")
  private Data publicData;

  private Customer customer;

  private String type;

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

  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
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
