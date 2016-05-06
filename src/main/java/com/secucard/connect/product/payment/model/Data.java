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

/**
 * Holds payment container details.
 */
public class Data {
  private String owner;

  private String iban;

  private String bic;

  private String bankname;

  public Data() {
  }

  public Data(String iban) {
    this.iban = iban;
  }

  public Data(String iban, String owner) {
    this.iban = iban;
    this.owner = owner;
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public String getIban() {
    return iban;
  }

  public void setIban(String iban) {
    this.iban = iban;
  }

  public String getBic() {
    return bic;
  }

  public void setBic(String bic) {
    this.bic = bic;
  }

  public String getBankname() {
    return bankname;
  }

  public void setBankname(String bankname) {
    this.bankname = bankname;
  }

  @Override
  public String toString() {
    return "Data{" +
        "owner='" + owner + '\'' +
        ", iban='" + iban + '\'' +
        ", bic='" + bic + '\'' +
        ", bankname='" + bankname + '\'' +
        '}';
  }
}