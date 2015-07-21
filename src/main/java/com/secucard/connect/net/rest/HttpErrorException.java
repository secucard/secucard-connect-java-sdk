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

package com.secucard.connect.net.rest;

/**
 * Exception indicating a HTTP request failed.
 * The {@link #getHttpStatus()} returns the HTTP status code.
 */
public class HttpErrorException extends Exception {
  private int httpStatus;
  private Object entity;

  public void setEntity(String entity) {
    this.entity = entity;
  }

  public Object getEntity() {
    return entity;
  }

  public int getHttpStatus() {
    return httpStatus;
  }

  public HttpErrorException(int httpStatus, Object entity) {
    this.httpStatus = httpStatus;
    this.entity = entity;
  }

  public HttpErrorException(int httpStatus) {
    this.httpStatus = httpStatus;
  }

  public HttpErrorException(Throwable cause, int httpStatus) {
    super(cause);
    this.httpStatus = httpStatus;
  }

  public HttpErrorException(String message, int httpStatus) {
    super(message);
    this.httpStatus = httpStatus;
  }

  public HttpErrorException(String message, Throwable cause, int httpStatus) {
    super(message, cause);
    this.httpStatus = httpStatus;
  }

  public HttpErrorException(String message, Throwable cause, int httpStatus, Object entity) {
    super(message, cause);
    this.httpStatus = httpStatus;
    this.entity = entity;
  }

  @Override
  public String toString() {
    return "HttpErrorException{" +
        "httpStatus=" + httpStatus +
        ", entity=" + entity +
        "} " + super.toString();
  }
}
