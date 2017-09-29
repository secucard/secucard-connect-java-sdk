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

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.secucard.connect.net.util.JsonMapper;
import com.secucard.connect.net.util.jackson.ObjectIdTypeResolver;
import com.secucard.connect.product.common.model.SecuObject;

import java.util.Date;

public class Event<T> extends SecuObject {
  public static final String TYPE_PROPERTY = "type";
  public static final String DATA_PROPERTY = "data";
  public static final String TARGET_PROPERTY = "target";
  public static final String OBJECT_PROPERTY_PREFIX = "event.";

  private String type;

  private String target;

  private Date created;

  @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = TARGET_PROPERTY)
  @JsonTypeIdResolver(ObjectIdTypeResolver.class)
  private T data = null;

  private String dataRaw = null;

  private JsonMapper jsonMapper = null;

  public String getTarget() {
    return target;
  }

  public void setTarget(String target) {
    this.target = target;
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

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  public String getDataRaw() {
    return dataRaw;
  }

  public void setDataRaw(String dataRaw) {
    this.dataRaw = dataRaw;
  }

  public JsonMapper getJsonMapper() {
    return jsonMapper;
  }

  public void setJsonMapper(JsonMapper jsonMapper) {
    this.jsonMapper = jsonMapper;
  }

  @Override
  public String toString() {
    return "Event{" +
        "type='" + type + '\'' +
        ", target='" + target + '\'' +
        ", created=" + created +
        ", data=" + data +
        '}';
  }
}
