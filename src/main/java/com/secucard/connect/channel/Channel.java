package com.secucard.connect.channel;

import com.secucard.connect.Callback;
import com.secucard.connect.ProductException;
import com.secucard.connect.SecuException;
import com.secucard.connect.ServiceOperations;
import com.secucard.connect.auth.AuthProvider;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.model.transport.Status;
import com.secucard.connect.util.Log;
import org.apache.commons.lang3.StringUtils;

/**
 * General base class for chanel implementations.
 */
public abstract class Channel implements ServiceOperations {

  protected PathResolver pathResolver = new PathResolver();
  protected final Log LOG = new Log(getClass());
  protected ExecutionListener executionListener;
  protected EventListener eventListener;
  protected JsonMapper jsonMapper = JsonMapper.get();
  protected String id;
  protected AuthProvider authProvider;

  /**
   * Open the channel and its resources.
   */
  public abstract void open();


  /**
   * Registers a listener which gets called when a server side or other event happens.
   * Server side events are not supported on any type of channels, e.g. REST based channels!
   */
  public void setEventListener(EventListener listener) {
    eventListener = listener;
  }

  public void setAuthProvider(AuthProvider authProvider) {
    this.authProvider = authProvider;
  }

  /**
   * Close channel and release resources.
   */
  public abstract void close();

  protected void onFailed(Callback callback, Throwable e) {
    if (callback != null) {
      try {
        callback.failed(e);
      } catch (Exception e1) {
        LOG.error("Client error", e);
      }
    }
  }

  protected <T> void onCompleted(Callback<T> callback, T result) {
    if (callback != null) {
      try {
        callback.completed(result);
      } catch (Exception e) {
        LOG.error("Client error", e);
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
