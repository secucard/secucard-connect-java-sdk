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

package com.secucard.connect.util;

import com.secucard.connect.auth.exception.AuthFailedException;
import com.secucard.connect.client.APIError;
import com.secucard.connect.client.AuthError;
import com.secucard.connect.client.ClientError;
import com.secucard.connect.client.NetworkError;
import com.secucard.connect.net.ServerErrorException;
import com.secucard.connect.net.stomp.client.StompError;
import com.secucard.connect.product.common.model.Status;

public class ExceptionMapper {
  /**
   * Translate or wrap any throwable into secucard exceptions.
   */
  public static RuntimeException map(Throwable throwable, String message) {
    if (throwable instanceof ClientError || throwable instanceof APIError || throwable instanceof NetworkError
        || throwable instanceof AuthError) {
      return (RuntimeException) throwable;
    }

    if (throwable instanceof StompError) {
      StompError se = (StompError) throwable;
      if (se.getHeaders() != null && "Bad CONNECT".contains(se.getHeaders().get("message"))) {
        return new AuthFailedException(se.getBody() + ", new authentication needed.");
      }
    }

    if (throwable instanceof ServerErrorException) {
      Status status = ((ServerErrorException) throwable).getStatus();

      if ("ProductInternalException".equalsIgnoreCase(status.getError())) {
        // map to internal exception
        return new ClientError(status.getErrorDetails(), throwable);
      }

      return new APIError(status.getError(), status.getCode(), status.getErrorDetails(), status.getErrorUser(),
          status.getSupportId(), throwable);
    }

    if (message == null) {
      message = throwable.getMessage();
    }

    return new ClientError(message, throwable);
  }

  @SuppressWarnings("unchecked")
  public static <T extends Exception> T unwrap(Throwable throwable, Class<T> ex) {
    if (ex.isInstance(throwable)) {
      return (T) throwable;
    }

    throwable = throwable.getCause();
    if (ex.isInstance(throwable)) {
      return (T) throwable;
    }

    throwable = throwable.getCause();
    if (ex.isInstance(throwable)) {
      return (T) throwable;
    }

    return null;
  }
}
