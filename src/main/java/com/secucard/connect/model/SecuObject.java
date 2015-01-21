package com.secucard.connect.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

public abstract class SecuObject implements Serializable {
  protected String id;

  @JsonIgnore
  protected String object;

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
}
