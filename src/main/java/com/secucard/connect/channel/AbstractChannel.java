package com.secucard.connect.channel;

import com.secucard.connect.Callback;
import com.secucard.connect.ProductException;
import com.secucard.connect.SecuException;
import com.secucard.connect.model.transport.Status;
import org.apache.commons.lang3.StringUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractChannel implements Channel {
  protected PathResolver pathResolver = new PathResolver();

  protected final Logger LOG = Logger.getLogger(getClass().getName());


  protected void onFailed(Callback callback, Throwable e) {
    if (callback != null) {
      try {
        callback.failed(e);
      } catch (Exception e1) {
        LOG.log(Level.SEVERE, "Client error", e);
      }
    }
  }

  protected <T> void onCompleted(Callback<T> callback, T result) {
    if (callback != null) {
      try {
        callback.completed(result);
      } catch (Exception e) {
        LOG.log(Level.SEVERE, "Client error", e);
      }
    }
  }

  protected RuntimeException translateError(Status status, Throwable cause) {
    if (StringUtils.startsWithIgnoreCase(status.getError(), "product")) {
      return new ProductException(status, cause);
    }
    return new SecuException(status, cause);
  }
}
