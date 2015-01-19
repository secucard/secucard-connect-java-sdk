package com.secucard.connect.model.services.idresult;

public class Request {
  private String object;
  private String id;

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

  @Override
  public String toString() {
    return "Request{" +
        "object='" + object + '\'' +
        ", id='" + id + '\'' +
        '}';
  }
}
