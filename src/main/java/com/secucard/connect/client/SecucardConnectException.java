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
 * Top level exception of the Java client. Should wrap all other.
 * Holds detailed information about the cause which should presented to the end user like a error code or a support id.
 * <p/>
 * If the code is {@link #INTERNAL} it means something unexpected happened, maybe caused by a bug, and you should display
 * a very generic message to the user or use the default given by {@link #getUserMessage()} and maybe notify yourself.
 */
public class SecucardConnectException extends RuntimeException {
  public static final String INTERNAL = "500";

  private String code = INTERNAL; // default for any unknown internal error
  private String userMessage = "Unknown error happened, please contact ... for assistance.";  //
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
   * Returns an unique error id to provide to the support.
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

  public SecucardConnectException(String message) {
    super(message);
  }

  public SecucardConnectException(String message, Throwable cause) {
    super(message, cause);
  }

  public SecucardConnectException(String code, String message, String userMessage, String serverError, String supportId,
                                  Throwable cause) {
    super(message, cause);
    this.code = code;
    this.userMessage = userMessage;
    this.serverError = serverError;
    this.supportId = supportId;
  }


  @Override
  public String toString() {
    return getClass().getName() + ": " + "code='" + code + '\'' +
        ", message='" + getMessage() + '\'' +
        ", userMessage='" + userMessage + '\'' +
        ", supportId='" + supportId + '\'' +
        ", serverError='" + serverError + '\'' +
        "} ";
  }
}
