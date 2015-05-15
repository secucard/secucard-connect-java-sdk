package com.secucard.connect.channel;

import com.secucard.connect.ServiceOperations;
import com.secucard.connect.auth.AuthProvider;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.util.Log;

/**
 * General base class for chanel implementations.
 */
public abstract class Channel implements ServiceOperations {
  public static final String STOMP = "STOMP";
  public static final String REST = "REST";

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
}
