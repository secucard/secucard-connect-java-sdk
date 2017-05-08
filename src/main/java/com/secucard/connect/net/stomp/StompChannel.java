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

package com.secucard.connect.net.stomp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.secucard.connect.client.Callback;
import com.secucard.connect.client.ClientContext;
import com.secucard.connect.client.ClientError;
import com.secucard.connect.net.Channel;
import com.secucard.connect.net.Options;
import com.secucard.connect.net.ServerErrorException;
import com.secucard.connect.net.stomp.client.Frame;
import com.secucard.connect.net.stomp.client.StompClient;
import com.secucard.connect.net.util.jackson.DynamicTypeReference;
import com.secucard.connect.product.common.model.Message;
import com.secucard.connect.product.common.model.ObjectList;
import com.secucard.connect.product.common.model.Result;
import com.secucard.connect.product.common.model.SecuObject;
import com.secucard.connect.product.general.model.Event;
import com.secucard.connect.util.ExceptionMapper;
import com.secucard.connect.util.Execution;
import com.secucard.connect.util.Log;
import com.secucard.connect.util.ThreadSleep;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class StompChannel extends Channel {
  private final static Log LOG = new Log(StompChannel.class);
  protected static final String HEADER_CORRELATION_ID = "correlation-id";
  protected static final String STATUS_OK = "ok";

  protected final Map<String, StompMessage> messages = new HashMap<>(20);
  protected final Configuration configuration;
  protected final StompClient stomp;
  protected String connectToken;
  private volatile boolean isConfirmed;
  private volatile boolean stopRefresh;
  private Thread refreshThread;
  private final String id;

  private final StatusHandler defaultStatusHandler = new StatusHandler() {
    @Override
    public boolean hasError(Message message) {
      return !STATUS_OK.equalsIgnoreCase(message.getStatus());
    }
  };

  public StompChannel(Configuration cfg, ClientContext context) {
    super(context);
    this.configuration = cfg;
    StompClient.Config stompCfg = new StompClient.Config(cfg.host, cfg.port, cfg.virtualHost,
        cfg.userId, cfg.password, cfg.heartbeatSec * 1000, cfg.socketTimeoutSec,
        cfg.messageTimeoutSec, cfg.connectionTimeoutSec);
    this.id = Integer.toString(hashCode());
    stomp = new StompClient(this.id, stompCfg, new DefaultEventListner());
  }

  /**
   * Must not be synchronized, because of session refresh in separate thread.
   * Just used one time when client is opened.
   */
  @Override
  public void open() {

    Throwable throwable = startSessionRefresh();
    if (throwable != null) {
      throw ExceptionMapper.map(throwable, null);
    }
  }


  @Override
  public synchronized void close() {
    stopRefresh = true;
    stomp.disconnect();
    LOG.debug("STOMP channel closed.");
  }

  @Override
  public <T> T request(Method method, Params params, Callback<T> callback) {
    Destination dest = createDestination(method, params);
    Message message = new Message<>(params.objectId, params.actionArg, params.queryParams, params.data);

    return sendMessage(dest, message, new MessageTypeRef(params.returnType), defaultStatusHandler, callback,
        params.options.timeOutSec, params.options.actionId);
  }

  @Override
  public <T> ObjectList<T> requestList(Method method, Params params, Callback<ObjectList<T>> callback) {
    Destination dest = createDestination(method, params);
    Message message = new Message<>(params.objectId, params.actionArg, params.queryParams, params.data);

    StatusHandler statusHandler = new StatusHandler() {
      @Override
      public boolean hasError(Message message) {
        // treat not found as ok here, no matches for query
        return !(STATUS_OK.equalsIgnoreCase(message.getStatus())
            || "ProductNotFoundException".equalsIgnoreCase(message.getError()));
      }
    };

    return sendMessage(dest, message, new MessageListTypeRef(params.returnType), statusHandler, callback,
        params.options.timeOutSec, null);
  }


  private Destination createDestination(Method method, Params params) {
    Destination dest;
    if (params.object != null) {
      dest = new Destination(params.object);
      dest.object = params.object;
    } else if (params.appId != null) {
      dest = new AppDestination(params.appId);
    } else {
      throw new IllegalArgumentException("Missing object spec or app id.");
    }

    if (method == Method.GET) {
      dest.command = "get:";
    } else if (method == Method.CREATE) {
      dest.command = "add:";
    } else if (method == Method.EXECUTE) {
      dest.command = "exec:";
    } else if (method == Method.UPDATE) {
      dest.command = "update:";
    } else if (method == Method.DELETE) {
      dest.command = "delete:";
    } else {
      throw new IllegalArgumentException("Invalid method arg");
    }

    dest.action = params.action;
    return dest;
  }


  /**
   * Provides the token used as login and password for  STOMP connect.
   */
  protected String getToken() {
    return context.tokenManager.getToken(false);
  }

  /**
   * Provides login, password for STOMP connect.
   */
  protected String[] getConnectCredentials() {
    return new String[]{configuration.userId, configuration.password};
  }

  /**
   * Connect to STOMP Server.
   * If the connection fails all resources are closed.
   *
   * @param token The token used as login/password. May be null.
   * @throws IllegalStateException If no connect credentials available.
   * @throws ClientError           If any  error happens.
   */
  private void connect(String token) {
    connectToken = token;
    String[] credentials = token == null ? getConnectCredentials() : new String[]{token, token};

    if (credentials == null || credentials.length != 2) {
      throw new IllegalStateException("Invalid connect credentials.");
    }

    stomp.connect(credentials[0], credentials[1]);

    if (eventListener != null) {
      eventListener.onEvent(StompEvents.STOMP_CONNECTED);
    }
  }

  private <T> T sendMessage(final Destination destinationSpec, final Object arg, final TypeReference returnType,
                            final StatusHandler statusHandler, Callback<T> callback, final Integer timeout,
                            final String actionId) {
    return new Execution<T>() {
      @Override
      protected T execute() {
        return doSendMessage(destinationSpec, arg, returnType, statusHandler, timeout, actionId);
      }
    }.start(callback);
  }

  private synchronized <T> T doSendMessage(Destination destinationSpec, Object arg, TypeReference returnType,
                                           StatusHandler statusHandler, Integer timeoutSec, String actionId) {
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
        LOG.info("Error disconnecting.", t);
      }
      connect(token);
    }

    Map<String, String> header = StompClient.createHeader(
        "reply-to", configuration.replyQueue,
        "content-type", "application/json",
        "user-id", token
    );

    if (destinationSpec instanceof AppDestination) {
      header.put("app-id", ((AppDestination) destinationSpec).appId);
    }

    if (actionId != null) {
      header.put("x-action", actionId);
    }

    String body = null;
    try {
      body = context.jsonMapper.map(arg);
      header.put("content-length", Integer.toString(body.getBytes("UTF-8").length));
    } catch (UnsupportedEncodingException e) {
      // should not happen
    } catch (IOException e) {
      throw new ClientError("Error marshalling data message data.", e);
    }

    String corrId = createCorrelationId(body);
    header.put("correlation-id", corrId);

    stomp.send(destinationSpec.toString(), body, header, timeoutSec);

    String answer = awaitAnswer(corrId, timeoutSec);
    Message<T> msg;
    try {
      msg = context.jsonMapper.map(answer, returnType);
    } catch (Exception e) {
      throw new ClientError("Error unmarshalling message.", e);
    }

    if (msg == null) {
      return null;
    }

    statusHandler.check(msg);

    isConfirmed = true;

    return msg.getData();
  }

  /**
   * Starts the session refresh loop thread. Blocks until the loop is really running and returns after that.
   *
   * @return Null if successfully started or an error if not.
   */
  public Throwable startSessionRefresh() {
    // first stop if running and wait until finished.
    stopRefresh = true;
    if (refreshThread != null && refreshThread.isAlive()) {
      LOG.debug("Refresh thread still running, wait for completion.");
      try {
        refreshThread.join();
      } catch (InterruptedException e) {
        // ignore
      }
    }

    final AtomicReference<Throwable> reference = new AtomicReference<>(null);

    final CountDownLatch latch = new CountDownLatch(1);
    stopRefresh = false;
    refreshThread = new Thread() {
      @Override
      public void run() {
        reference.set(runSessionRefresh(latch));
        latch.countDown();
      }
    };

    refreshThread.setDaemon(true);
    refreshThread.start();

    // let current thread
    try {
      latch.await();
    } catch (InterruptedException e) {
      // ignore
    }

    // must return null if no error happened
    return reference.get();
  }

  /**
   * Sends a confirmations within an fixed interval that this client is alive.
   * But is able to skip confirmation if other already confirmed this (setting isConfirmed to true).
   * Always keeps trying to refresh next time even if an attempt failed.
   *
   * @param countDownLatch A latch to release if successfully executed.
   */
  private Throwable runSessionRefresh(CountDownLatch countDownLatch) {
    LOG.info("Session refresh loop started.");
    boolean initial = true;
    do {
      try {
        LOG.debug("Try session refresh.");
        Options options = Options.getDefault();
        options.timeOutSec = 5; // should timeout sooner as by config to detect connection failure
        request(Method.EXECUTE, new Params(new String[]{"auth", "sessions"}, "me", "refresh", null, null, Result.class,
            options), null);
        isConfirmed = false;
        LOG.info("Session refresh sent.");
      } catch (Throwable t) {
        LOG.info("Session refresh failed.");
        if (initial) {
          // first invocation after connect, let client know something is going wrong
          return t;
        }
        // try next time
      }

      // releases latch
      if (initial) {
        countDownLatch.countDown();
        initial = false;
      }

      // sleep until next refresh, reset wait time if anybody confirmed session for us so we can sleep longer
      new ThreadSleep() {
        @Override
        protected boolean reset() {
          if (isConfirmed) {
            isConfirmed = false;
            return true;
          }
          return false;
        }

        @Override
        protected boolean cancel() {
          return stopRefresh;
        }
      }.execute(configuration.heartbeatSec * 1000, 500, TimeUnit.MILLISECONDS);

    } while (!stopRefresh);

    LOG.info("Session refresh stopped.");

    return null;
  }

  private static void putMessage(String id, String body, Map<String, StompMessage> messages) {
    if (id != null && !id.isEmpty()) {
      StompMessage msg = new StompMessage(id, body);
      StompMessage m = messages.put(msg.id, msg);
      if (m != null) {
        throw new IllegalArgumentException("Invalid correlation id, message with this id already exists.");
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

  private String createCorrelationId(String str) {
    return id + "-" + str.hashCode() + "-" + System.currentTimeMillis();
  }

  private String awaitAnswer(final String id, Integer timeoutSec) {
    if (timeoutSec == null) {
      timeoutSec = configuration.messageTimeoutSec;
    }
    long maxWaitTime = System.currentTimeMillis() + timeoutSec * 1000;
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
      throw new MessageTimeoutException("No answer for " + id + " received within " + timeoutSec + "s.");
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
      super(Message.class, new TypeInfo(ObjectList.class, type));
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
        throw new ServerErrorException(message);
      }
    }
  }

  // Default Stomp Message Handling ------------------------------------------------------------------------------------

  private class DefaultEventListner implements StompClient.Listener {

    @Override
    public void onMessage(Frame frame) {
      String correlationId = frame.getHeaders().get(HEADER_CORRELATION_ID);
      String body = frame.getBody();

      if (body == null) {
        return;
      }

      if (correlationId != null) {
        synchronized (messages) {
          putMessage(correlationId, body, messages);
        }
      } else if (eventListener != null) {
        // this is an STOMP event message, no direct correlation to a request
        LOG.debug("STOMP event message received: ", body);

        // todo: event type testing

        Object event = null;
        try {
          // we expect Event type at first
          event = context.jsonMapper.map(body, Event.class);
        } catch (Exception e) {
          // ignore
        }

        if (event != null) {
          eventListener.onEvent(event);
        } else {
          // try to map into any known object
          try {
            event = context.jsonMapper.map(body);
            eventListener.onEvent(event);
          } catch (Exception e) {
            LOG.error("STOMP message received but unable to convert: ", body, "; ", e.getMessage());
          }
        }
      }
    }

    @Override
    public void onDisconnect() {
      if (eventListener != null) {
        eventListener.onEvent(StompEvents.STOMP_DISCONNECTED);
      }
    }
  }


  protected class Destination {
    String[] object;
    String command;  // standard api command like defined by constants above
    String action;

    public Destination(String[] object) {
      this.object = object;
    }

    public String toString() {
      String dest = configuration.basicDestination + "api:" + command;

      if (object != null) {
        dest += buildTarget(object, '.');
      }

      if (action != null) {
        dest += "." + action;
      }

      return dest;
    }
  }

  protected class AppDestination extends Destination {
    String appId;

    public AppDestination(String appId) {
      super(null);
      this.appId = appId;
    }

    public String toString() {
      return configuration.basicDestination + "app:" + action;
    }
  }

  /**
   * STOMP configuration. Supported properties are:
   * <p/>
   * - stomp.host, STOMP host.<br/>
   * - stomp.virtualHost, STOMP virtual host.<br/>
   * - stomp.port, STOMP port.<br/>
   * - stomp.destination, Base path of the secucard STOMP API.<br/>
   * - stomp.user, Login, just for tests.<br/>
   * - stomp.pwd, Password, just for tests.<br/>
   * - stomp.replyQueue, The default queue for all STOMP messages.<br/>
   * - stomp.messageTimeoutSec, Timeout for awaiting message receipts and also message responses.
   * An error is raised after. 0 means no waiting.<br/>
   * - stomp.connectTimeoutSec, Timeout for trying to connect to STOMP server. 0 means no waiting.<br/>
   * - stomp.socketTimeoutSec, Max time the receiving socket is allowed to block when waiting for any input.
   * This timeout mainly determines the time needed to detect broken socket connections, so short timeouts are desirable
   * but obviously also increases number of unnecessary performed timeout handling circles.<br/>
   * - stomp.heartbeatSec, The interval in sec a heart beat signal is sent to the Stomp server to verify the
   * client is still alive. Helps to cleanup connections to dead clients.<br/>
   * - stomp.maxMessageAgeSec, Max age of received STOMP messages in the systems message box before they get
   * deleted. Keeps the message queue clean, usually messages should not get very old in the box, if a message
   * reaches this max age its very likely that nobody is interested or a problem exist and therefore we can remove.<br/>
   * - stomp.disconnectOnError, STOMP channel will be disconnected or not when a ERROR frame was received
   * In our environment receiving an error means a non recoverable error condition caused by bugs or configuration problems,
   * so it's better to close this automatically to prevent resource leaking.
   */
  public static class Configuration {
    private final String host;
    private final int port;
    private final String password;
    private final String virtualHost;
    private final int heartbeatSec;
    private final String userId;
    private final String replyQueue;
    private final int connectionTimeoutSec;
    private final int messageTimeoutSec;
    private final int maxMessageAgeSec;
    private final int socketTimeoutSec;
    private final String basicDestination;

    public Configuration(Properties properties) {
      this.host = properties.getProperty("stomp.host");
      this.port = Integer.parseInt(properties.getProperty("stomp.port"));
      this.password = properties.getProperty("stomp.pwd");
      this.virtualHost = properties.getProperty("stomp.virtualHost");
      this.heartbeatSec = Integer.parseInt(properties.getProperty("stomp.heartbeatSec"));
      this.userId = properties.getProperty("stomp.user");
      this.replyQueue = properties.getProperty("stomp.replyQueue");
      this.connectionTimeoutSec = Integer.parseInt(properties.getProperty("stomp.connectTimeoutSec"));
      this.messageTimeoutSec = Integer.parseInt(properties.getProperty("stomp.messageTimeoutSec"));
      this.maxMessageAgeSec = Integer.parseInt(properties.getProperty("stomp.maxMessageAgeSec"));
      this.socketTimeoutSec = Integer.parseInt(properties.getProperty("stomp.socketTimeoutSec"));

      String property = properties.getProperty("stomp.destination");
      if (!property.endsWith("/")) {
        property += "/";
      }
      this.basicDestination = property;
    }


    @Override
    public String toString() {
      return "STOMP Configuration{" +
          "host='" + host + '\'' +
          ", port=" + port +
          ", password='" + password + '\'' +
          ", virtualHost='" + virtualHost + '\'' +
          ", heartbeatSec=" + heartbeatSec +
          ", userId='" + userId + '\'' +
          ", replyQueue='" + replyQueue + '\'' +
          ", connectionTimeoutSec=" + connectionTimeoutSec +
          ", messageTimeoutSec=" + messageTimeoutSec +
          ", maxMessageAgeSec=" + maxMessageAgeSec +
          ", socketTimeoutSec=" + socketTimeoutSec +
          ", basicDestination='" + basicDestination + '\'' +
          '}';
    }
  }
}
