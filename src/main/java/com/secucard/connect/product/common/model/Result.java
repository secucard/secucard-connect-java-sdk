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
