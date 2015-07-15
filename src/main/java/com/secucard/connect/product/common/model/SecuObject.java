package com.secucard.connect.product.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;

public abstract class SecuObject {
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
