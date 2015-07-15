package com.secucard.connect.product.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Generic result container used as payload (data) in stomp response messages.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Result {

  private String result;

  private String request;

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  public String getRequest() {
    return request;
  }

  public void setRequest(String request) {
    this.request = request;
  }
}
