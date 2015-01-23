package com.secucard.connect.model.general;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.secucard.connect.model.SecuObject;

import java.util.Date;

public class Event<T> extends SecuObject {

  private String type;

  private boolean liveMode;

  private Date created;

  private T data;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public boolean isLiveMode() {
    return liveMode;
  }

  public void setLiveMode(boolean liveMode) {
    this.liveMode = liveMode;
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
        "id='" + id + '\'' +
        "type='" + type + '\'' +
        ", liveMode=" + liveMode +
        ", created=" + created +
        ", data=" + data +
        '}';
  }
}
