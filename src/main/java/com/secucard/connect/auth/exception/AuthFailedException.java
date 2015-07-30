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

package com.secucard.connect.auth.exception;

import com.secucard.connect.client.AuthError;

/**
 * Indicates an authorization attempt failed due missing or invalid authentication data.
 * Typically this kind of error is caused by wrong API usage or alike, something that is wrong implemented.
 * <p/>
 * Inspect {@link #getError()} for the general error "type". <br/>
 * Inspect {@link #getMessage()} for a more detailed description oft the error.
 */
public class AuthFailedException extends AuthError {
  private String error;

  /**
   * Returns an error type string.
   */
  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public AuthFailedException(String message, String error) {
    super(message);
    this.error = error;
  }

  public AuthFailedException(String message) {
    super(message);
  }

  public AuthFailedException(String message, Throwable cause) {
    super(message, cause);
  }

  @Override
  public String getLocalizedMessage() {
    return error + ", " + getMessage();
  }
}
