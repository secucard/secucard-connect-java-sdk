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
