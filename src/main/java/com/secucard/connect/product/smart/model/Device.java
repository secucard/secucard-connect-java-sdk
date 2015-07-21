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

import com.secucard.connect.product.common.model.SecuObject;
import com.secucard.connect.product.general.model.Merchant;

public class Device extends SecuObject {

  private String type;

  private Merchant merchant;

  public Device() {
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

  @Override
  public String toString() {
    return "Device{" +
        "type='" + type + '\'' +
        '}';
  }
}
