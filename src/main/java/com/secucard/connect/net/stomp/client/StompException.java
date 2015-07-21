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
package com.secucard.connect.net.stomp.client;

import java.util.Map;


/**
 * Exception wrapping a STOMP error frame.
 */
public class StompException extends RuntimeException {
  private String body;
  private Map<String, String> headers;

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public void setHeaders(Map<String, String> headers) {
    this.headers = headers;
  }

  public StompException(String body, Map<String, String> headers) {
    this.body = body;
    this.headers = headers;
  }

  public StompException(String message, String body, Map<String, String> headers) {
    super(message);
    this.body = body;
    this.headers = headers;
  }

  public StompException() {
  }

  public StompException(String message) {
    super(message);
  }

  public StompException(Throwable cause) {
    super(cause);
  }

  @Override
  public String toString() {
    return super.toString() + ": " +
        "body='" + body + '\'' +
        ", headers=" + headers;
  }
}
