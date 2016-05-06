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

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Generic list type for any type of objects. <br/>
 * Pass "new {@literal javax.ws.rs.core.GenericType<ObjectList<Transaction>>() {}}"
 * to {@link javax.ws.rs.core.Response#readEntity(javax.ws.rs.core.GenericType)} when using JAX-RS implementations. <br/>
 * Or pass "new {@literal com.fasterxml.jackson.core.type.TypeReference<ObjectList<Transaction>>()}"
 * to according methods of {@link com.fasterxml.jackson.databind.ObjectMapper}.
 */
public class ObjectList<T> {

  @JsonProperty("scroll_id")
  private String scrollId;

  @JsonProperty("count")
  private int totalCount;

  @JsonProperty("data")
  private List<T> list;

  public ObjectList() {
  }

  public String getScrollId() {
    return scrollId;
  }

  public void setScrollId(String scrollId) {
    this.scrollId = scrollId;
  }

  public int getCount() {
    return list == null ? 0 : list.size();
  }

  public int getTotalCount() {
    return totalCount;
  }

  public List<T> getList() {
    return list;
  }

  public void setList(List<T> list) {
    this.list = list;
  }

  @Override
  public String toString() {
    return "ObjectList{" + "scrollId='" + scrollId + '\'' + ", count=" + totalCount + ", list=" + list + '}';
  }
}
