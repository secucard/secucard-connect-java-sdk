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
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Generic list for any type T of objects. Provides some additional methods for getting the actual objects or the list count.
 */
public class ObjectList<T> {

  @JsonProperty("scroll_id")
  protected String scrollId;

  @JsonProperty("count")
  protected int totalCount;

  @JsonProperty("data")
  protected List<T> list;

  /**
   * Returns a id associated with this result.
   */
  public String getScrollId() {
    return scrollId;
  }

  /**
   * Return the number of contained object in this list.
   */
  @JsonIgnore
  public int getCount() {
    return list == null ? 0 : list.size();
  }

  /**
   * Return the number of actually available objects (maybe more then getCount()) for the query which produces this result.
   */
  public int getTotalCount() {
    return totalCount;
  }

  /**
   * Returns the actual object instances.
   */
  public List<T> getList() {
    return list;
  }

  @Override
  public String toString() {
    return "ObjectList{" + "scrollId='" + scrollId + '\'' + ", totalCount=" + totalCount + ", list=" + list + '}';
  }
}
