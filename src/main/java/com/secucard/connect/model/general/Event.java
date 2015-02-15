package com.secucard.connect.model.general;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.secucard.connect.model.SecuObject;

import java.util.Date;

public class Event<T> extends SecuObject {
  public static final String TYPE_PROPERTY = "type";
  public static final String DATA_PROPERTY = "data";
  public static final String TARGET_PROPERTY = "target";
  public static final String OBJECT_PROPERTY_PREFIX = "event.";

  private String type;

  private String target;

  private Date created;

  private T data;

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
