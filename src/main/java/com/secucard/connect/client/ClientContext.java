package com.secucard.connect.client;

import com.secucard.connect.auth.TokenManager;
import com.secucard.connect.event.EventDispatcher;
import com.secucard.connect.net.Channel;
import com.secucard.connect.net.util.JsonMapper;

import java.util.Map;

/**
 * Bundles all instances to work within client and service.
 */
public class ClientContext {
  public TokenManager tokenManager;
  public EventDispatcher eventDispatcher;
  public ExceptionHandler exceptionHandler;
  public Map<String, Channel> channels;
  public String defaultChannel;
  public JsonMapper jsonMapper;
  public Object runtimeContext;
}
