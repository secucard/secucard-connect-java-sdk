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

import com.secucard.connect.client.APIError;
import com.secucard.connect.client.ClientError;
import com.secucard.connect.client.NetworkError;
import com.secucard.connect.net.ServerErrorException;
import com.secucard.connect.product.common.model.Status;

public class ExceptionMapper {
  /**
   * Translate or wrap any throwable into secucard exceptions.
   */
  public static RuntimeException map(Throwable throwable, String message) {
    if (throwable instanceof ClientError || throwable instanceof APIError || throwable instanceof NetworkError) {
      return (RuntimeException) throwable;
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
}
