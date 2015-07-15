package com.secucard.connect.product.general.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
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
