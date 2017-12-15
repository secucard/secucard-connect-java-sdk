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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.product.common.model.SecuObject;
import com.secucard.connect.product.general.model.Merchant;
import com.secucard.connect.product.general.model.Store;

import java.util.Date;

public class Device extends SecuObject {

  private Date created;
  private Boolean online;
  private int number;
  private String vendor;

  @JsonProperty("vendor_uid")
  private String vendorUid;

  @JsonProperty("user_pin")
  private String userPin;
  private String description;
  private Store store;
  private com.secucard.connect.product.general.model.Device device;
  private Merchant merchant;
  private String type;

  public Date getCreated() {
    return created;
  }
  public void setCreated(Date created) {
    this.created = created;
  }

  public Boolean getOnline() {
    return online;
  }
  public void setOnline(Boolean online) {
    this.online = online;
  }

  public int getNumber() {
    return number;
  }
  public void setNumber(int number) {
    this.number = number;
  }

  public String getVendor() {
    return vendor;
  }
  public void setVendor(String vendor) {
    this.vendor = vendor;
  }

  public String getVendorUid() {
    return vendorUid;
  }
  public void setVendorUid(String vendorUid) {
    this.vendorUid = vendorUid;
  }

  public String getUserPin() {
    return userPin;
  }
  public void setUserPin(String userPin) {
    this.userPin = userPin;
  }

  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }

  public Store getStore() {
    return store;
  }
  public void setStore(Store store) {
    this.store = store;
  }

  public com.secucard.connect.product.general.model.Device getDevice() {
    return device;
  }
  public void setDevice(com.secucard.connect.product.general.model.Device device) {
    this.device = device;
  }

  public Merchant getMerchant() {
    return merchant;
  }

  public void setMerchant(Merchant merchant) {
    this.merchant = merchant;
  }

  public Device(String id) {
    this.id = id;
  }

  public Device(String id, String type) {
    this.id = id;
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Device() {
  }

  @Override
  public String toString() {
    return "Device{" +
            "type='" + type + '\'' +
            ", vendorUid=" + vendorUid + '\'' +
            ", vendor=" + vendor + '\'' +
            ", description=" + description + '\'' +
            ", store=" + store + '\'' +
            ", merchant=" + merchant + '\'' +
    '}';
  }
}