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

package com.secucard.connect.client;

/**
 * Indicates business related errors happening when a API call could not performed properly.<br/>
 * Happens for example when input data have wrong format or if not enough balance exist for a product.<br/>
 * Holds detailed information about the cause which should presented to the end user like a error code or a support id.
 */
public class APIError extends RuntimeException {
  private String code;
  private String userMessage;
  private String supportId;
  private String serverError;

  /**
   * Returns an unique error code.
   */
  public String getCode() {
    return code;
  }

  /**
   * Return a user friendly message describing the problem.
   */
  public String getUserMessage() {
    return userMessage;
  }

  /**
   * Returns an unique error id to provide to the support facility.
   */
  public String getSupportId() {
    return supportId;
  }

  /**
   * Returns message describing the problem.
   */
  @Override
  public String getMessage() {
    return super.getMessage();
  }

  /**
   * Returns the original error type the server was submitting.
   * May give additional hints about the problem.
   */
  public String getServerError() {
    return serverError;
  }

  public APIError(String serverError, String code, String message, String userMessage, String supportId, Throwable cause) {
    super(message, cause);
    this.code = code;
    this.userMessage = userMessage;
    this.supportId = supportId;
    this.serverError = serverError;
  }

  @Override
  public String toString() {
    return "APIError{" +
        "code='" + code + '\'' +
        ", message='" + getMessage() + '\'' +
        ", userMessage='" + userMessage + '\'' +
        ", supportId='" + supportId + '\'' +
        ", serverError='" + serverError + '\'' +
        "}";
  }
}
