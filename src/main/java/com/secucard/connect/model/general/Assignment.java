package com.secucard.connect.model.general;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.loyalty.Card;

import java.io.Serializable;
import java.util.Date;

public class Assignment implements Serializable {
  private Date created;

  private String type;

  private boolean owner;

  @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
          property = SecuObject.OBJECT_PROPERTY)
  @JsonSubTypes({
          @JsonSubTypes.Type(value = Merchant.class, name = Merchant.OBJECT),
          @JsonSubTypes.Type(value = AccountDevice.class, name = AccountDevice.OBJECT),
          @JsonSubTypes.Type(value = Card.class, name = Card.OBJECT)})
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
