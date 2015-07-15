/*
 * Copyright (c) 2014 secucard AG. All rights reserved
 */

package com.secucard.connect.product.general.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class OpenHours implements Serializable {

  @JsonProperty
  private Date open;

  @JsonProperty
  private Date close;

  public Date getOpen() {
    return open;
  }

  public void setOpen(Date open) {
    this.open = open;
  }

  public Date getClose() {
    return close;
  }

  public void setClose(Date close) {
    this.close = close;
  }

  public static class Date implements Serializable {

    @JsonProperty
    private int day;

    @JsonProperty
    private String time;

    public int getDay() {
      return day;
    }

    public void setDay(int day) {
      this.day = day;
    }

    public String getTime() {
      return time;
    }

    public void setTime(String time) {
      this.time = time;
    }
  }
}
