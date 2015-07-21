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

import com.secucard.connect.client.SecucardConnectException;
import com.secucard.connect.net.ServerErrorException;
import com.secucard.connect.product.common.model.Status;

public class ExceptionMapper {
  /**
   * Translate or wrap any throwable into our main exception.
   */
  public static SecucardConnectException map(Throwable throwable) {
    if (throwable instanceof SecucardConnectException) {
      return (SecucardConnectException) throwable;
    }

    if (throwable instanceof ServerErrorException) {
      Status status = ((ServerErrorException) throwable).getStatus();

      if (status.getError().equalsIgnoreCase("ProductInternalException")) {
        // map to internal exception
        return new SecucardConnectException(status.getErrorDetails(), throwable);
      }

      return new SecucardConnectException(status.getCode(), status.getErrorDetails(), status.getErrorUser(),
          status.getError(), status.getSupportId(), throwable);
    }

    return new SecucardConnectException(throwable.getMessage(), throwable);
  }
}
