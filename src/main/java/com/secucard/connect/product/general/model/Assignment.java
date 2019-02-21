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

package com.secucard.connect.product.general.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.secucard.connect.product.common.model.SecuObject;
import com.secucard.connect.product.loyalty.model.Card;
import java.io.Serializable;
import java.util.Date;

public class Assignment implements Serializable {

  private Date created;

  private String type;

  private boolean owner;

  @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = SecuObject.OBJECT_PROPERTY)
  @JsonSubTypes({
      @JsonSubTypes.Type(value = Merchant.class, name = "general.merchants"),
      @JsonSubTypes.Type(value = AccountDevice.class, name = "general.accountdevices"), @JsonSubTypes.Type(value = Card.class, name = "loyalty.cards")
  })
  private SecuObject assign;

  public SecuObject getAssign() {
    return assign;
  }

  public void setAssign(SecuObject assign) {
    this.assign = assign;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public boolean isOwner() {
    return owner;
  }

  public void setOwner(boolean owner) {
    this.owner = owner;
  }
}
