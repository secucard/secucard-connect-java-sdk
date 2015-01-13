package com.secucard.connect.model.general;

import com.secucard.connect.model.SecuObject;

public class Event extends SecuObject {

  public Event(String content) {
    this.content = content;
  }

  private String content;


  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  @Override
  public String toString() {
    return "Event{" +
        "content='" + content + '\'' +
        '}';
  }
}
