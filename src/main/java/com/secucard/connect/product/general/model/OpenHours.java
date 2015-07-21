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
