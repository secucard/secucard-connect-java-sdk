package com.secucard.connect.channel.stomp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.secucard.connect.Callback;
import com.secucard.connect.ConnectionException;
import com.secucard.connect.auth.AuthException;
import com.secucard.connect.auth.AuthProvider;
import com.secucard.connect.channel.Channel;
import com.secucard.connect.event.Events;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.QueryParams;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.transport.Message;
import com.secucard.connect.stomp.Frame;
import com.secucard.connect.stomp.StompClient;
import com.secucard.connect.stomp.StompException;
import com.secucard.connect.util.Execution;
import com.secucard.connect.util.jackson.DynamicTypeReference;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class StompChannel extends Channel {
  protected static final String HEADER_CORRELATION_ID = "correlation-id";
  protected static final String STATUS_OK = "ok";

  protected final Map<String, StompMessage> messages = new HashMap<>(20);
  protected final Configuration configuration;
  protected final StompClient stomp;
  protected String connectToken;

  private final StatusHandler defaultStatusHandler = new StatusHandler() {
    @Override
    public boolean hasError(Message message) {
      return !message.getStatus().equalsIgnoreCase(STATUS_OK);
    }
  };

  public StompChannel(String id, Configuration cfg) {
    this.configuration = cfg;
    this.id = id;
    StompClient.Config stompCfg = new StompClient.Config(cfg.host, cfg.port, cfg.virtualHost,
        cfg.userId, cfg.password, cfg.heartbeatMs, cfg.useSsl, cfg.socketTimeoutSec,
        cfg.messageTimeoutSec, cfg.connectionTimeoutSec);
    stomp = new StompClient(id, stompCfg, new DefaultEventListner());
  }

  @Override
  public synchronized void open() {
  }


  @Override
  public synchronized void close() {
    stomp.disconnect();
  }

  @Override
  public <T> T getObject(final Class<T> type, String objectId, final Callback<T> callback) {
    return sendMessage(new StandardDestination(StandardDestination.GET, type), new Message<T>(objectId),
        new MessageTypeRef(type), callback);
  }

  @Override
  public <T> ObjectList<T> findObjects(final Class<T> type, QueryParams queryParams, Callback<ObjectList<T>> callback) {
    Message message = new Message();
    message.setQuery(queryParams);

    StatusHandler statusHandler = new StatusHandler() {
      @Override
      public boolean hasError(Message message) {
        // treat not found as ok here, no matches for query
        return !(STATUS_OK.equalsIgnoreCase(message.getStatus())
            || "ProductNotFoundException".equalsIgnoreCase(message.getError()));
      }
    };

    return sendMessage(new StandardDestination(StandardDestination.GET, type), message, new MessageListTypeRef(type),
        statusHandler, callback);
  }

  @Override
  public <T> T createObject(T object, Callback<T> callback) {
    return sendMessage(new StandardDestination(StandardDestination.ADD, object.getClass()),
        new Message<>(object), new MessageTypeRef(object.getClass()), callback);
  }

  @Override
  public <T extends SecuObject> T updateObject(T object, Callback<T> callback) {
    return sendMessage(new StandardDestination(StandardDestination.UPDATE, object.getClass()),
        new Message<>(object.getId(), object), new MessageTypeRef(object.getClass()), callback);
  }

  @Override
  public <T> T updateObject(Class product, String objectId, String action, String actionArg, Object arg,
                            Class<T> returnType, Callback<T> callback) {
    return sendMessage(new StandardDestination(StandardDestination.UPDATE, product, action),
        new Message<>(objectId, actionArg, arg), new MessageTypeRef(returnType), callback);
  }

  @Override
  public void deleteObject(Class type, String objectId, Callback callback) {
    sendMessage(new StandardDestination(StandardDestination.DELETE, type), new Message<>(objectId),
        new MessageTypeRef(type), callback);
  }

  @Override
  public void deleteObject(Class product, String objectId, String action, String actionArg, Callback<?> callback) {
    sendMessage(new StandardDestination(StandardDestination.DELETE, product, action), new Message<>(objectId, actionArg),
        new MessageTypeRef(product), callback);
  }

  @Override
  public <T> T execute(String appId, String action, Object arg, Class<T> returnType, Callback<T> callback) {
    return sendMessage(new AppDestination(appId, action), arg, new MessageTypeRef(returnType), callback);
  }

  @Override
  public <T> T execute(Class product, String objectId, String action, String actionArg, Object arg, Class<T> returnType,
                       Callback<T> callback) {
    return sendMessage(new StandardDestination(StandardDestination.EXEC, product, action),
        new Message<>(objectId, actionArg, arg), new MessageTypeRef(returnType), callback);
  }

  public void setAuthProvider(AuthProvider authProvider) {
    this.authProvider = authProvider;
  }

  /**
   * Returns the token used as login + password for  STOMP connect.
   */
  protected String getToken() {
    return authProvider.getToken().getAccessToken();
  }

  /**
   * Returns login, password for  STOMP connect.
   */
  protected String[] getConnectCredentials() {
    return new String[]{configuration.userId, configuration.password};
  }

  /**
   * Connect to STOMP Server.
   * If the connection fails all resources are closed.
   *
   * @param token The token used as login/password. May be null.
   * @throws java.lang.IllegalStateException           If no connect credentials available.
   * @throws com.secucard.connect.auth.AuthException   If the provided credentials are invalid.
   * @throws com.secucard.connect.stomp.StompException If any STOMP related error happens.
   */
  private void connect(String token) {
    connectToken = token;
    String[] credentials = token == null ? getConnectCredentials() : new String[]{token, token};

    if (credentials == null || credentials.length != 2) {
      throw new IllegalStateException("Invalid connect credentials.");
    }

    try {
      stomp.connect(credentials[0], credentials[1]);
    } catch (StompException e) {
      if (isConnectionError(e.getHeaders(), e.getBody())) {
        throw new AuthException("Authorization failed: " + e.getBody());
      }
    }
    eventListener.onEvent(Events.STOMP_CONNECTED);
  }

  private <T> T sendMessage(StandardDestination destinationSpec, Object arg, TypeReference returnType,
                            final Callback<T> callback) {
    return sendMessage(destinationSpec, arg, returnType, defaultStatusHandler, callback);
  }

  private <T> T sendMessage(final StandardDestination destinationSpec, final Object arg,
                            final TypeReference returnType, final StatusHandler statusHandler, Callback<T> callback) {
    return new Execution<T>() {
      @Override
      protected T execute() {
        return sendMessage(destinationSpec, arg, returnType, statusHandler);
      }
    }.start(callback);
  }

  private synchronized <T> T sendMessage(StandardDestination destinationSpec, Object arg,
                                         TypeReference returnType, StatusHandler statusHandler) {
    String token = getToken();

    // auto-connect or reconnect if token has changed since last connect
    if (!stomp.isConnected() || (token != null && !token.equals(connectToken))) {
      if (stomp.isConnected()) {
        LOG.debug("Reconnect due token change.");
      }
      try {
        stomp.disconnect();
      } catch (Throwable t) {
        // just log...
        LOG.info("Error disconnecting due ", t);
      }
      try {
        connect(token);
      } catch (Exception e) {
        throw new ConnectionException("Error reconnecting before send.", e);
      }
    }

    String corrId = createCorrelationId();

    Map<String, String> header = StompClient.createHeader(
        "reply-to", configuration.replyQueue,
        "correlation-id", corrId,
        "persistent", "true",
        "content-type", "application/json",
        "user-id", token
    );

    if (destinationSpec instanceof AppDestination) {
      header.put("app-id", ((AppDestination) destinationSpec).appId);
    }

    String body = null;
    try {
      body = jsonMapper.map(arg);
      header.put("content-length", Integer.toString(body.getBytes("UTF-8").length));
    } catch (UnsupportedEncodingException e) {
      // should not happen
    } catch (IOException e) {
      throw new RuntimeException("Error marshalling data message data", e);
    }

    stomp.send(destinationSpec.toString(), body, header);

    String answer = awaitAnswer(corrId);
    Message<T> msg;
    try {
      msg = jsonMapper.map(answer, returnType);
    } catch (Exception e) {
      throw new RuntimeException("Error unmarshalling message.", e);
    }

    if (msg == null) {
      return null;
    }

    statusHandler.check(msg);

    if (executionListener != null) {
      executionListener.executed(this);
    }

    return msg.getData();
  }


  /**
   * Returns if a stomp error is a connection related error.
   */
  private static boolean isConnectionError(Map<String, String> headers, String body) {
    if (headers != null && headers.containsKey("message")) {
      if (headers.get("message").contains("Bad CONNECT")) {
        return true;
      }
    }
    return false;
  }

  private static void putMessage(String id, String body, Map<String, StompMessage> messages) {
    if (id != null && !id.isEmpty()) {
      StompMessage msg = new StompMessage(id, body);
      StompMessage m = messages.put(msg.id, msg);
      if (m != null) {
        // ignore for now
      }
    }
  }

  private static String pullMessage(String id, Map<String, StompMessage> messages, int maxMessageAgeSec) {
    long t = System.currentTimeMillis();
    Iterator<Map.Entry<String, StompMessage>> it = messages.entrySet().iterator();
    while (it.hasNext()) {
      StompMessage message = it.next().getValue();
      long ageSec = (t - message.receiveTime) / 1000;
      boolean match = message.id.equalsIgnoreCase(id);
      if (match || ageSec > maxMessageAgeSec) {
        it.remove();
        if (match) {
          return message.body == null ? "" : message.body;
        }
      }
    }
    return null;
  }

  private String createCorrelationId() {
    return (id == null ? Integer.toString(hashCode()) : id) + "-" + System.currentTimeMillis();
  }

  private String awaitAnswer(final String id) {
    long maxWaitTime = System.currentTimeMillis() + configuration.messageTimeoutSec * 1000;
    String msg = null;
    while (System.currentTimeMillis() <= maxWaitTime) {
      synchronized (messages) {
        if (messages.containsKey(id)) {
          msg = pullMessage(id, messages, configuration.maxMessageAgeSec);
          break;
        }
      }
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        // will be stopped anyway
      }
    }

    if (msg == null) {
      throw new MessageTimeoutException("No answer for " + id + " received in time.");
    }

    return msg;
  }


  // Inner Classes -----------------------------------------------------------------------------------------------------


  /**
   * The default {@code Message<T>} type reference object.
   */
  protected static class MessageTypeRef extends DynamicTypeReference<Void> {
    public MessageTypeRef(Class type) {
      super(Message.class, type);
    }
  }

  /**
   * The {@code Message<ObjectList<T>>} type reference object.
   */
  protected static class MessageListTypeRef extends DynamicTypeReference<Void> {
    public MessageListTypeRef(Class type) {
      super(Message.class, new DynamicTypeReference.TypeInfo(ObjectList.class, type));
    }
  }

  private static class StompMessage {
    public final String id;
    public final String body;
    public final long receiveTime;

    private StompMessage(String id, String body) {
      this.id = id;
      this.body = body;
      receiveTime = System.currentTimeMillis();
    }
  }

  protected abstract class StatusHandler {
    public abstract boolean hasError(Message message);

    public void check(Message message) {
      if (hasError(message)) {
        throw translateError(message, null);
      }
    }
  }

  // Default Stomp Message Handling ------------------------------------------------------------------------------------

  private class DefaultEventListner implements StompClient.Listener {

    @Override
    public void onMessage(Frame frame) {
      String correlationId = frame.getHeaders().get(HEADER_CORRELATION_ID);
      String body = frame.getBody();
      if (correlationId != null) {
        synchronized (messages) {
          putMessage(correlationId, body, messages);
        }
      }

      if (eventListener != null && correlationId == null) {
        // this is an STOMP event message, no direct correlation to a request
        Object event = null;
        try {
          event = jsonMapper.mapEvent(body);
        } catch (Exception e) {
          // ignore
        }

        if (event != null) {
          eventListener.onEvent(event);
          return;
        }

        try {
          event = jsonMapper.map(body);
          eventListener.onEvent(event);
        } catch (Exception e) {
          LOG.debug("STOMP message received but unable to convert: ", body, "; ", e.getMessage());
        }
      }
    }

    @Override
    public void onError(Frame frame) {
      if (isConnectionError(frame.getHeaders(), frame.getBody())) {
        eventListener.onEvent(new Events.AuthorizationFailed("STOMP authorization failed, reason: " + frame.getBody()));
        return;
      }

      if (eventListener != null) {
        Map<String, String> headers = frame.getHeaders();
        headers.put("body", frame.getBody());
        eventListener.onEvent(new Events.Error("STOMP error happened.", headers));
      }
    }

    @Override
    public void onDisconnect() {
      if (eventListener != null) {
        eventListener.onEvent(Events.STOMP_DISCONNECTED);
      }
    }
  }


  protected class StandardDestination {
    static final String GET = "get:";
    static final String UPDATE = "update:";
    static final String ADD = "add:";
    static final String DELETE = "delete:";
    static final String EXEC = "exec:";
    static final String DEST_PREFIX = "api:";

    String command;  // standard api command like defined by constants above
    String method;
    Class type;

    StandardDestination(String command, Class type) {
      this.command = command;
      this.type = type;
    }

    StandardDestination(String command, Class type, String method) {
      this.command = command;
      this.method = method;
      this.type = type;
    }

    public String toString() {
      String dest = getBasicDestination() + DEST_PREFIX;

      dest += command;

      if (type != null) {
        dest += pathResolver.resolveType(type, '.');
      }

      if (method != null) {
        dest += "." + method;
      }

      return dest;
    }

    protected String getBasicDestination() {
      return configuration.basicDestination;
    }
  }

  protected class AppDestination extends StandardDestination {
    static final String DEST_PREFIX = "app:";
    String appId;

    AppDestination(String appId, String method) {
      super(null, null, method);
      this.appId = appId;
    }

    public String toString() {
      return getBasicDestination() + DEST_PREFIX + method;
    }
  }

  public static class Configuration {
    private final String host;
    private final int port;
    private final String password;
    private final String virtualHost;
    private final int heartbeatMs;
    private final boolean useSsl;
    private final boolean autoConnect;
    private final String userId;
    private final String replyQueue;
    private final int connectionTimeoutSec;
    private final int messageTimeoutSec;
    private final int maxMessageAgeSec;
    private final int socketTimeoutSec;
    private final String basicDestination;

    public Configuration(String host, String virtualHost, int port,
                         String basicDestination,
                         String userId, String password,
                         boolean useSsl, String replyQueue, int connectionTimeoutSec,
                         int messageTimeoutSec, int maxMessageAgeSec, int socketTimeoutSec, int heartbeatMs) {
      this.host = host;
      this.port = port;
      this.password = password;
      this.virtualHost = virtualHost;
      this.heartbeatMs = heartbeatMs;
      this.useSsl = useSsl;
      this.userId = userId;
      this.replyQueue = replyQueue;
      this.connectionTimeoutSec = connectionTimeoutSec;
      this.messageTimeoutSec = messageTimeoutSec;
      this.maxMessageAgeSec = maxMessageAgeSec;
      this.socketTimeoutSec = socketTimeoutSec;
      this.autoConnect = true;

      if (!basicDestination.endsWith("/")) {
        basicDestination += "/";
      }
      this.basicDestination = basicDestination;
    }
  }
}
