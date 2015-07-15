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
