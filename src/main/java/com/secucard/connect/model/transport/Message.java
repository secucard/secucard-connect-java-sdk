package com.secucard.connect.model.transport;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.secucard.connect.model.QueryParams;

/**
 * General message container used by stomp messages.
 *
 * @param <T> The actual type of the payload data.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Message<T> extends Status {

  private String pid;

  private String sid;

  private QueryParams query;

  private T data;

  public Message() {
  }

  public Message(T data) {
    this.data = data;
  }

  public Message(String pid) {
    this.pid = pid;
  }

  public Message(String pid, T data) {
    this.pid = pid;
    this.data = data;
  }

  public Message(String pid, String sid, T data) {
    this.pid = pid;
    this.sid = sid;
    this.data = data;
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  public String getPid() {
    return pid;
  }

  public void setPid(String pid) {
    this.pid = pid;
  }

  public String getSid() {
    return sid;
  }

  public void setSid(String sid) {
    this.sid = sid;
  }

  public QueryParams getQuery() {
    return query;
  }

  public void setQuery(QueryParams query) {
    this.query = query;
  }

  @Override
  public String toString() {
    return "Message{" +
        "pid='" + pid + '\'' +
        ", sid='" + sid + '\'' +
        ", query=" + query +
        ", data=" + data +
        "} " + super.toString();
  }
}
