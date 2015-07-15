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

  private int count;

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
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public List<T> getList() {
    return list;
  }

  public void setList(List<T> list) {
    this.list = list;
  }

  @Override
  public String toString() {
    return "ObjectList{" + "scrollId='" + scrollId + '\'' + ", count=" + count + ", list=" + list + '}';
  }
}
