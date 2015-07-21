/*
 * Copyright (c) 2015. hp.weber GmbH & Co secucard KG (www.secucard.com)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.secucard.connect.product.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;

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

  public Message(String pid, String sid, QueryParams query, T data) {
    this.pid = pid;
    this.sid = sid;
    this.query = query;
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
