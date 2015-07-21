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

package com.secucard.connect.product.services.model.idrequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.product.general.model.Contact;

public class Person {
  @JsonProperty("transaction_id")
  private String transactionId;

  @JsonProperty("redirect_url")
  private String redirectUrl;

  private String status;

  @JsonProperty("owner_transaction_id")
  private String ownerTransactionId;

  private Contact contact = new Contact();

  private String custom1;
  private String custom2;
  private String custom3;
  private String custom4;
  private String custom5;

  public Contact getContact() {
    return contact;
  }

  public void setContact(Contact contact) {
    this.contact = contact;
  }

  public String getTransactionId() {
    return transactionId;
  }

  public void setTransactionId(String transactionId) {
    this.transactionId = transactionId;
  }

  public String getRedirectUrl() {
    return redirectUrl;
  }

  public void setRedirectUrl(String redirectUrl) {
    this.redirectUrl = redirectUrl;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getOwnerTransactionId() {
    return ownerTransactionId;
  }

  public void setOwnerTransactionId(String ownerTransactionId) {
    this.ownerTransactionId = ownerTransactionId;
  }

  public String getCustom1() {
    return custom1;
  }

  public void setCustom1(String custom1) {
    this.custom1 = custom1;
  }

  public String getCustom2() {
    return custom2;
  }

  public void setCustom2(String custom2) {
    this.custom2 = custom2;
  }

  public String getCustom3() {
    return custom3;
  }

  public void setCustom3(String custom3) {
    this.custom3 = custom3;
  }

  public String getCustom4() {
    return custom4;
  }

  public void setCustom4(String custom4) {
    this.custom4 = custom4;
  }

  public String getCustom5() {
    return custom5;
  }

  public void setCustom5(String custom5) {
    this.custom5 = custom5;
  }

  @Override
  public String toString() {
    return "Person{" +
        "transactionId='" + transactionId + '\'' +
        ", redirectUrl='" + redirectUrl + '\'' +
        ", status='" + status + '\'' +
        ", ownerTransactionId='" + ownerTransactionId + '\'' +
        ", contact=" + contact +
        ", custom1='" + custom1 + '\'' +
        ", custom2='" + custom2 + '\'' +
        ", custom3='" + custom3 + '\'' +
        ", custom4='" + custom4 + '\'' +
        ", custom5='" + custom5 + '\'' +
        '}';
  }
}
