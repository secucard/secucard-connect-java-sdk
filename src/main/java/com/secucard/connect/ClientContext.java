package com.secucard.connect;

import com.secucard.connect.auth.AuthProvider;
import com.secucard.connect.channel.Channel;
import com.secucard.connect.event.EventDispatcher;
import com.secucard.connect.storage.DataStorage;
import com.secucard.connect.util.ResourceDownloader;
import com.secucard.connect.util.ThreadLocalUtil;

/**
 * Context instance holding all necessary beans used in client in services.
 */
public class ClientContext {
  public static final String STOMP = "stomp";
  public static final String REST = "rest";

  protected DataStorage dataStorage;
  protected Channel restChannel;
  protected Channel stompChannel;
  protected AuthProvider authProvider;
  protected ClientConfiguration config;
  protected String clientId;
  protected ExceptionHandler exceptionHandler;
  protected Object runtimeContext;
  protected ResourceDownloader resourceDownloader;
  protected EventDispatcher eventDispatcher;

  /**
   * Obtain the current client context instance..
   */
  public static ClientContext get() {
    return (ClientContext) ThreadLocalUtil.get(ClientContext.class.getName());
  }

  public EventDispatcher getEventDispatcher() {
    return eventDispatcher;
  }

  public DataStorage getDataStorage() {
    return dataStorage;
  }

  public AuthProvider getAuthProvider() {
    return authProvider;
  }

  public ClientConfiguration getConfig() {
    return config;
  }

  public String getClientId() {
    return clientId;
  }

  public ExceptionHandler getExceptionHandler() {
    return exceptionHandler;
  }

  public void setExceptionHandler(ExceptionHandler exceptionHandler) {
    this.exceptionHandler = exceptionHandler;
  }

  public Object getRuntimeContext() {
    return runtimeContext;
  }

  public ResourceDownloader getResourceDownloader() {
    return resourceDownloader;
  }

  /**
   * Return a channel to the given name.
   *
   * @param name The channel name or null for default channel.
   *             Valid names are: {@link ClientContext#STOMP}, {@link ClientContext#REST}.
   * @return Null if the requested channel is not available or disabled by config, the channel instance else.
   * @throws java.lang.IllegalArgumentException if the name is not valid.
   */
  public Channel getChannel(String name) {
    if (name == null) {
      name = config.getDefaultChannel();
    }

    if (REST.equals(name)) {
      return restChannel;
    }

    if (STOMP.equals(name)) {
      return stompChannel;
    }

    throw new IllegalArgumentException("invalid channel name " + name);
  }
}

