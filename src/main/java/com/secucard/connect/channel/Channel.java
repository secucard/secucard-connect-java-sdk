package com.secucard.connect.channel;

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
  public static final String STOMP = "stomp";
  public static final String REST = "rest";

  protected PathResolver pathResolver = new PathResolver();
  protected final Log LOG = new Log(getClass());
  protected EventListener<Object> eventListener;
  protected JsonMapper jsonMapper = JsonMapper.get();
  protected String id;
  protected AuthProvider authProvider;

  /**
   * Open the channel and its resources.
   */
  public abstract void open();


  /**
   * Registers a listener which gets called when a server side or other event happens.
   * Server side events may not be supported by some channels, e.g. REST based channels!
   *
   * @param listener The listener to
   */
  public void setEventListener(EventListener<Object> listener) {
    eventListener = listener;
  }

  public void setAuthProvider(AuthProvider authProvider) {
    this.authProvider = authProvider;
  }

  /**
   * Close channel and release resources.
   */
  public abstract void close();

  /**
   * Inspects the given status and returns an appropriate exception.
   * At the moment only product related errors are recognized all other return an general exceptions.
   *
   * @param status The error status details.
   * @param cause  The error cause.
   * @return Translated exception instance witch cause attached as root cause.
   */
  protected RuntimeException translateError(Status status, Throwable cause) {
    if (StringUtils.startsWithIgnoreCase(status.getError(), "product")) {
      return new ProductException(status, cause);
    }
    return new SecuException(status, cause);
  }
}
