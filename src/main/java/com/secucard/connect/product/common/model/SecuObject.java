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

package com.secucard.connect.product.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Map;

/**
 * Base class for all product resource models.
 */
public abstract class SecuObject implements Serializable {
  public static final String OBJECT_PROPERTY = "object";
  public static final String OBJECT_FIELD = "OBJECT";
  public static final String ID_PROPERTY = "id";

  protected String id;

  // ignore for now
  @JsonIgnore
  private Map metaData;

  private String object;

  public String getObject() {
    return object;
  }

  public void setObject(String object) {
    this.object = object;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Map getMetaData() {
    return metaData;
  }

  public void setMetaData(Map metaData) {
    this.metaData = metaData;
  }


}
