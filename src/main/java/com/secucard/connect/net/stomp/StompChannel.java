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
import com.secucard.connect.SecucardConnect;
import com.secucard.connect.auth.exception.AuthFailedException;
import com.secucard.connect.client.AuthError;
import com.secucard.connect.client.Callback;
import com.secucard.connect.client.ClientContext;
import com.secucard.connect.client.ClientError;
import com.secucard.connect.client.NetworkError;
import com.secucard.connect.client.OfflineCache;
import com.secucard.connect.client.OfflineMessage;
import com.secucard.connect.net.Channel;
import com.secucard.connect.net.Options;
import com.secucard.connect.net.ServerErrorException;
import com.secucard.connect.net.stomp.client.Frame;
import com.secucard.connect.net.stomp.client.NoReceiptException;
import com.secucard.connect.net.stomp.client.StompClient;
import com.secucard.connect.net.util.jackson.DynamicTypeReference;
import com.secucard.connect.product.common.model.Message;
import com.secucard.connect.product.common.model.ObjectList;
import com.secucard.connect.product.common.model.Result;
import com.secucard.connect.product.general.model.Event;
import com.secucard.connect.product.smart.TransactionService;
import com.secucard.connect.product.smart.model.Transaction;
import com.secucard.connect.util.ExceptionMapper;
import com.secucard.connect.util.Execution;
import com.secucard.connect.util.Log;
import com.secucard.connect.util.ThreadSleep;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.json.JSONObject;

public class StompChannel extends Channel {

  private static final Log LOG = new Log(StompChannel.class);
  protected static final String HEADER_CORRELATION_ID = "correlation-id";
  protected static final String STATUS_OK = "ok";
  public static final String PREFIX_STX_OFFLINE = "STX_OFFLINE_";

  protected final Map<String, StompMessage> messages = new HashMap<>(20);
  protected final Configuration configuration;
  protected final StompClient stomp;
  protected final OfflineCache offlineCache;
  private final String id;
  private final StatusHandler defaultStatusHandler = new StatusHandler() {
    @Override
    public boolean hasError(Message message) {
      return !STATUS_OK.equalsIgnoreCase(message.getStatus());
    }
  };

  protected String connectToken;
  private volatile boolean isConfirmed;
  private volatile boolean stopRefresh;
  private volatile boolean stopOfflineMessagesThread;
  private volatile boolean offlineModeActive = false;
  private Thread refreshThread;
  private Thread offlineMessagesThread;

  public StompChannel(Configuration cfg, ClientContext context) {
    super(context);
    this.configuration = cfg;
    this.offlineCache = new OfflineCache(cfg.offlineCacheDir);
    StompClient.Config stompCfg = new StompClient.Config(
        cfg.host,
        cfg.port,
        cfg.virtualHost,
        cfg.userId,
        cfg.password,
        cfg.heartbeatSec * 1000,
        cfg.socketTimeoutSec,
        cfg.messageTimeoutSec,
        cfg.connectionTimeoutSec
    );
    this.id = Integer.toString(hashCode());
    stomp = new StompClient(this.id, stompCfg, new DefaultEventListner());
  }

  /**
   * Must not be synchronized, because of session refresh in separate thread. Just used one time when client is opened.
   */
  @Override
  public void open() {
    Throwable throwable = startSessionRefresh();
    if (throwable != null) {
      throw ExceptionMapper.map(throwable, null);
    }

    sendLogMessage(this.configuration.toString(), "INFO");

    throwable = createOfflineMessagesThread();
    if (throwable != null) {
      throw ExceptionMapper.map(throwable, null);
    }
  }

  @Override
  public synchronized void close() {
    stopRefresh = true;
    stopOfflineMessagesThread = true;
    stomp.disconnect();
    LOG.debug("STOMP channel closed.");
  }

  @Override
  public <T> T request(Method method, Params params, Callback<T> callback) {
    Destination dest = createDestination(method, params);
    Message message = new Message<>(params.objectId, params.actionArg, params.queryParams, params.data);

    return sendMessage(dest, message, new MessageTypeRef(params.returnType), null, callback, params.options.timeOutSec, params.options.actionId);
  }

  @Override
  public <T> ObjectList<T> requestList(Method method, Params params, Callback<ObjectList<T>> callback) {
    Destination dest = createDestination(method, params);
    Message message = new Message<>(params.objectId, params.actionArg, params.queryParams, params.data);

    StatusHandler statusHandler = new StatusHandler() {
      @Override
      public boolean hasError(Message message) {
        // treat not found as ok here, no matches for query
        return !(STATUS_OK.equalsIgnoreCase(message.getStatus()) || "ProductNotFoundException".equalsIgnoreCase(message.getError()));
      }
    };

    return sendMessage(dest, message, new MessageListTypeRef(params.returnType), statusHandler, callback, params.options.timeOutSec, null);
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
   * Connect to STOMP Server. If the connection fails all resources are closed.
   *
   * @param token The token used as login/password. May be null.
   * @throws IllegalStateException If no connect credentials available.
   * @throws ClientError If any  error happens.
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

  private <T> T sendMessage(final Destination destinationSpec, final Message arg, final TypeReference returnType,
                            final StatusHandler statusHandler, Callback<T> callback, final Integer timeout,
                            final String actionId) {
    return new Execution<T>() {
      @Override
      protected T execute() {
        return doSendMessage(destinationSpec, arg, returnType, statusHandler, timeout, actionId);
      }
    }.start(callback);
  }

  private synchronized <T> T doSendMessage(Destination destinationSpec, Message arg, TypeReference returnType,
                                           StatusHandler statusHandler, Integer timeoutSec, String actionId) {
    String body = this.convertBodyToString(arg);
    String corrId = createCorrelationId(body);
    Map<String, String> header = this.collectHeaders(destinationSpec, body, actionId, corrId);

    int defaultReceiptTimeoutSec = 0;
    if (this.usePrintingDefaultReceipt(destinationSpec, arg)) {
      defaultReceiptTimeoutSec = configuration.defaultReceiptTimeoutSec;
    }

    if (offlineModeActive && this.useOfflineMode(destinationSpec, arg)) {
      return this.saveOfflineMessage(arg, header, body, corrId, destinationSpec, returnType);
    }

    try {
      String token = getToken();
      this.autoConnect(token);

      return doSendMessageNow(header, body, corrId, destinationSpec.toString(), returnType, statusHandler, timeoutSec, defaultReceiptTimeoutSec);
    } catch (NetworkError|NoReceiptException error) {
      if (this.useOfflineMode(destinationSpec, arg)) {
        offlineModeActive = true;
        LOG.warn("Offline mode is active, queueing current message.");
        if (!"general".equals(destinationSpec.object[0]) && !"apps".equals(destinationSpec.object[1])) {
          this.sendLogMessage("Offline mode is active, queueing current message.", "WARNING");
        }

        return this.saveOfflineMessage(arg, header, body, corrId, destinationSpec, returnType);
      }

      throw error;
    }
  }

  private  <T> T saveOfflineMessage(Message arg, Map<String, String> header, String body, String corrId, Destination destinationSpec, TypeReference returnType) {
    // save request
    OfflineMessage offlineMessage = new OfflineMessage();
    offlineMessage.corrId = corrId;
    offlineMessage.body = body;
    offlineMessage.header = header;
    offlineMessage.destination = destinationSpec.toString();
    offlineMessage.returnType = returnType.toString();
    offlineMessage.id = arg.getPid();
    offlineCache.save(corrId, offlineMessage);

    if ("smart".equals(destinationSpec.object[0]) && "transactions".equals(destinationSpec.object[1])) {
      if ("exec:".equals(destinationSpec.command) && "start".equals(destinationSpec.action)) {
        if (ping()) {
          offlineModeActive = false;
          open();
        }
        throw new MessageTimeoutException(SecucardConnect.PRINT_OFFLINE_RECEIPT_ERROR_MESSAGE);
      }

      Transaction res;
      if (arg.getData() instanceof Transaction) {
        res = (Transaction) arg.getData();
      } else {
        res = new Transaction();
      }

      if (res.getId() == null || res.getId().isEmpty()) {
        res.setId(PREFIX_STX_OFFLINE + corrId);
      }

      res.setStatus("offline");
      return (T) res;
    }

    return null;
  }

  private boolean useOfflineMode(Destination destinationSpec, Message arg) {
    // STOMP connection is inactive?
    if (stomp.isConnected()) {
      return false;
    }

    // Option "stomp.offline.enabled" is active?
    if (!configuration.enableOfflineMode) {
      return false;
    }

    // Endpoint is "smart.transactions" or general.apps?
    if ((!"smart".equals(destinationSpec.object[0]) || !"transactions".equals(destinationSpec.object[1])) &&
        (!"general".equals(destinationSpec.object[0]) || !"apps".equals(destinationSpec.object[1]) || "ping".equals(arg.getSid()))
    ) {
      return false;
    }

    // Is it not a "GET" call?
    if ("get:".equals(destinationSpec.command)) {
      return false;
    }

    // Is it the "exec:start" method AND it's "cash"?
    if ("exec:".equals(destinationSpec.command) && "start".equals(destinationSpec.action) && !TransactionService.TYPE_CASH.equals(arg.getSid())) {
      return false;
    }

    // Is it not the "exec:Diagnosis", "exec:EndofDay", "exec:cancelTrx"?
    if ("exec:".equals(destinationSpec.command) && ("Diagnosis".equals(destinationSpec.action) || "EndofDay".equals(destinationSpec.action)
        || "cancelTrx".equals(destinationSpec.action))) {
      return false;
    }

    return true;
  }

  private void autoConnect(String token) {
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
  }

  private boolean usePrintingDefaultReceipt(Destination destinationSpec, Message arg) {

    // Option "receipt.default.timeout" is active?
    if (configuration.defaultReceiptTimeoutSec <= 0) {
      return false;
    }

    // Endpoint is "smart.transactions"?
    if (!"smart".equals(destinationSpec.object[0]) || !"transactions".equals(destinationSpec.object[1])) {
      return false;
    }

    // Is it the "exec:start" method?
    if (!"exec:".equals(destinationSpec.command) || !"start".equals(destinationSpec.action)) {
      return false;
    }

    // it's "cash"?
    return TransactionService.TYPE_CASH.equals(arg.getSid());

  }

  public synchronized <T> T doSendMessageNow(Map<String, String> header, String body, String corrId,
                                              String destination, TypeReference returnType, StatusHandler statusHandler,
                                              Integer timeoutSec, Integer defaultReceiptTimeoutSec) {
    String token = getToken();
    autoConnect(token);

    header.put("user-id", token);
    try {
      stomp.send(destination, body, header, timeoutSec);
    } catch (Throwable e) {
      sendLogMessage(e.toString(), "ERROR");
      throw e;
    }
    String answer = awaitAnswer(corrId, timeoutSec, defaultReceiptTimeoutSec);
    Message<T> msg;
    try {
      msg = context.jsonMapper.map(answer, returnType);
    } catch (Exception e) {
      throw new ClientError("Error unmarshalling message.", e);
    }

    if (msg == null) {
      return null;
    }

    if (statusHandler == null) {
      statusHandler = defaultStatusHandler;
    }
    statusHandler.check(msg);

    isConfirmed = true;

    return msg.getData();
  }

  private String convertBodyToString(Message arg) {
    String body = "";
    try {
      body = context.jsonMapper.map(arg);
    } catch (UnsupportedEncodingException e) {
      // should not happen
    } catch (IOException e) {
      throw new ClientError("Error marshalling data message data.", e);
    }

    return body;
  }

  private Map<String, String> collectHeaders(Destination destinationSpec, String body, String actionId, String corrId) {
    Map<String, String> header = StompClient.createHeader(
        "reply-to",
        configuration.replyQueue,
        "content-type",
        "application/json",
        "correlation-id",
        corrId,
        "content-length",
        Integer.toString(body.getBytes(StandardCharsets.UTF_8).length)
    );

    // optional "app-id"
    if (destinationSpec instanceof AppDestination) {
      header.put("app-id", ((AppDestination) destinationSpec).appId);
    }

    // optional: "x-action"
    if (configuration.enableOfflineMode && actionId == null) {
      actionId = corrId;
    }

    if (actionId != null) {
      header.put("x-action", actionId);
    }

    return header;
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
   * Sends a confirmations within an fixed interval that this client is alive. But is able to skip confirmation if other already confirmed this
   * (setting isConfirmed to true). Always keeps trying to refresh next time even if an attempt failed.
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
        request(
            Method.EXECUTE,
            new Params(
                new String[]{"auth", "sessions"},
                "me",
                "refresh",
                null,
                null,
                Result.class,
                options
            ),
            null
        );
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

  private Throwable createOfflineMessagesThread() {
    // Check config
    if (!this.configuration.enableOfflineMode) {
      return null;
    }

    // first stop if running and wait until finished.
    stopOfflineMessagesThread = true;
    if (offlineMessagesThread != null && offlineMessagesThread.isAlive()) {
      LOG.debug("Send offline messages thread still running, wait for completion.");
      try {
        offlineMessagesThread.join();
      } catch (InterruptedException e) {
        // ignore
      }
    }

    final AtomicReference<Throwable> reference = new AtomicReference<>(null);

    final CountDownLatch latch = new CountDownLatch(1);
    stopOfflineMessagesThread = false;
    offlineMessagesThread = new Thread() {
      @Override
      public void run() {
        reference.set(runOfflineMessagesThread(latch));
        latch.countDown();
      }
    };

    offlineMessagesThread.setDaemon(true);
    offlineMessagesThread.start();

    // let current thread
    try {
      latch.await();
    } catch (InterruptedException e) {
      // ignore
    }

    // must return null if no error happened
    return reference.get();
  }

  private Throwable runOfflineMessagesThread(CountDownLatch countDownLatch) {
    LOG.info("Send offline messages loop started.");
    boolean initial = true;
    do {
      try {
        LOG.debug("Checking for open offline messages.");
        Options options = Options.getDefault();
        options.timeOutSec = 5; // should timeout sooner as by config to detect connection failure
        this.sendOfflineMessages(null, null);
      } catch (Throwable t) {
        LOG.info("Sending offline message failed.");
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
        protected boolean cancel() {
          return stopOfflineMessagesThread;
        }
      }.execute(configuration.offlineMessagesLoopSleepSec * 1000, 500, TimeUnit.MILLISECONDS);

    } while (!stopOfflineMessagesThread);

    LOG.info("Session refresh stopped.");

    return null;
  }

  /**
   * Method to send offline smart transaction messages (needs settings for "stomp.offline.*")
   *
   * @return int Number of processed offline messages
   */
  public int sendOfflineMessages() {
    // Check config
    if (!this.configuration.enableOfflineMode) {
      return -1;
    }

    // Load files
    File[] files = this.offlineCache.getFiles();
    if (files == null) {
      return 0;
    }

    HashMap<String, String> idMappingTable = new HashMap<>();

    // Process files
    int count = 0;
    for (File file : files) {
      LOG.info("Try to send offline message: " + file.getName());
      this.sendLogMessage("Try to send offline message: "+ file.getName(), "WARNING");
      try {
        // Load message
        Object data = this.offlineCache.get(file.getName());

        if (!(data instanceof OfflineMessage)) {
          throw new RuntimeException("Offline message has a Wrong format:" + file.getName());
        }

        if (((OfflineMessage) data).destination.equals("smart")) {
          String id = ((OfflineMessage) data).id;
          String body = ((OfflineMessage) data).body;

          // Replace temporary id
          if (id.startsWith(PREFIX_STX_OFFLINE) && idMappingTable.get(id) != null) {
            body = body.replace(id, idMappingTable.get(id));
            id = idMappingTable.get(id);
          }

          // Send message
          Transaction trans = doSendMessageNow(((OfflineMessage) data).header, body, ((OfflineMessage) data).corrId,
                                      ((OfflineMessage) data).destination, new MessageTypeRef(Transaction.class), null,
                                      ((OfflineMessage) data).timeoutSec > 0 ? ((OfflineMessage) data).timeoutSec : 30, 0);

          // Store new id for the next requests
          if (id.startsWith(PREFIX_STX_OFFLINE)) {
            idMappingTable.put(id, trans.getId());
          }
        } else if (((OfflineMessage) data).destination.equals("general")) {
          // Send message
          doSendMessageNow(((OfflineMessage) data).header, ((OfflineMessage) data).body, ((OfflineMessage) data).corrId,
                                      ((OfflineMessage) data).destination, new MessageTypeRef(Result.class), null,
                                      5, 0);
        }

        // Clean up
        file.delete();
        count++;
      } catch (RuntimeException e) {
        LOG.info("Could not send offline message: " + file.getName());
        this.sendLogMessage("Could not send offline message: "+ file.getName() + " " + e.toString(), "WARNING");
        break;
//
//        // Move the message to "failed" folder?
//        if (!(e instanceof AuthError) && !(e instanceof NetworkError) && failedMessagesPath != null && failedMessagesPath.length() > 0) {
//          LOG.warn("Sending offline message failed: " + file.getName());
//
//          // Check folder
//          File failedMessagesDir = new File(failedMessagesPath);
//          if (!failedMessagesDir.exists()) {
//            failedMessagesDir.mkdirs();
//          }
//
//          // Rename
//          file.renameTo(new File(failedMessagesDir.getPath() + "/" + file.getName()));
//        }
      }
    }

    return count;
  }

  private String createCorrelationId(String str) {
    return System.currentTimeMillis() + "-" + id + "-" + str.hashCode();
  }

  private String awaitAnswer(final String id, Integer timeoutSec, int defaultReceiptTimeoutSec) {
    if (timeoutSec == null) {
      timeoutSec = configuration.messageTimeoutSec;
    }
    long maxWaitTime = System.currentTimeMillis() + timeoutSec * 1000;
    long maxReceiptWaitTime = 0;
    if (defaultReceiptTimeoutSec > 0) {
      maxReceiptWaitTime = System.currentTimeMillis() + defaultReceiptTimeoutSec * 1000;
    }
    String msg = null;
    while (System.currentTimeMillis() <= maxWaitTime) {
      synchronized (messages) {
        if (messages.containsKey(id)) {
          msg = pullMessage(id, messages, configuration.maxMessageAgeSec);
          break;
        }
      }

      if (maxReceiptWaitTime > 0 && System.currentTimeMillis() > maxReceiptWaitTime) {
        throw new MessageTimeoutException(SecucardConnect.PRINT_OFFLINE_RECEIPT_ERROR_MESSAGE);
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
  public static class MessageTypeRef extends DynamicTypeReference<Void> {
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

        Event event = null;
        try {
          // we expect Event type at first
          event = context.jsonMapper.map(body, Event.class);
        } catch (Exception e) {
          // ignore
        }

        if (event != null) {
          // set raw data
          try {
            JSONObject object = new JSONObject(body);
            event.setDataRaw(object.getJSONArray("data").toString());
            event.setJsonMapper(context.jsonMapper);
          } catch (Exception e) {
            LOG.error("STOMP message received but unable to convert: ", body, "; ", e.getMessage());
          }

          eventListener.onEvent(event);
        } else {
          // try to map into any known object
          try {
            Object event2 = context.jsonMapper.map(body);
            eventListener.onEvent(event2);
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
    private final boolean enableOfflineMode;
    private final String offlineCacheDir;
    private final int offlineMessagesLoopSleepSec;
    private final boolean offlineMessagesDisableThread;
    private final int defaultReceiptTimeoutSec;

    public Configuration(Properties properties) {
      this.host = properties.getProperty("stomp.host");
      this.port = getIntOption(properties, "stomp.port", 1, 65535, 61614);
      this.password = properties.getProperty("stomp.pwd");
      this.virtualHost = properties.getProperty("stomp.virtualHost");
      this.heartbeatSec = getIntOption(properties, "stomp.heartbeatSec", 5, 120, 30);
      this.userId = properties.getProperty("stomp.user");
      this.replyQueue = properties.getProperty("stomp.replyQueue");
      this.connectionTimeoutSec = getIntOption(properties, "stomp.connectTimeoutSec", 1, 120, 30);
      this.messageTimeoutSec = getIntOption(properties, "stomp.messageTimeoutSec", 1, 300, 120);
      this.maxMessageAgeSec = getIntOption(properties, "stomp.maxMessageAgeSec", 1, 600, 360);
      this.socketTimeoutSec = getIntOption(properties, "stomp.socketTimeoutSec", 1, 120, 30);
      this.enableOfflineMode = getBoolOption(properties, "stomp.offline.enabled", false);
      this.offlineMessagesDisableThread = getBoolOption(properties, "stomp.offline.disableThread", false);
      this.offlineMessagesLoopSleepSec = getIntOption(properties, "stomp.offline.sleepSec", 60, 900, 60);
      this.defaultReceiptTimeoutSec = getIntOption(properties, "receipt.default.timeout", 1, 120, 30);
      this.basicDestination = getPathOption(properties, "stomp.destination", "/exchange/connect.api/");
      this.offlineCacheDir = getPathOption(properties, "stomp.offline.dir", ".scc-offline/");
    }

    private int getIntOption(Properties properties, String configName, int minValue, int maxValue, int defaultValue) {
      int value = defaultValue;
      String property = properties.getProperty(configName);

      if (property != null && !property.isEmpty()) {
        value = Integer.parseInt(property);
      }

      if (value < minValue || value > maxValue) {
        value = defaultValue;
      }

      return value;
    }

    private boolean getBoolOption(Properties properties, String configName, boolean defaultValue) {
      String property = properties.getProperty(configName);

      if (property != null && !property.isEmpty()) {
        return Boolean.parseBoolean(property);
      }

      return defaultValue;
    }

    private String getPathOption(Properties properties, String configName, String defaultValue) {
      String property = properties.getProperty(configName);

      if (property != null && !property.isEmpty()) {
        if (!property.endsWith("/")) {
          property += "/";
        }
        return property;
      }

      return defaultValue;
    }

    @Override
    public String toString() {
      return "STOMP Configuration{" +
          "host='" + host + '\'' +
          ", port=" + port +
          ", virtualHost='" + virtualHost + '\'' +
          ", heartbeatSec=" + heartbeatSec +
          ", replyQueue='" + replyQueue + '\'' +
          ", connectionTimeoutSec=" + connectionTimeoutSec +
          ", messageTimeoutSec=" + messageTimeoutSec +
          ", maxMessageAgeSec=" + maxMessageAgeSec +
          ", socketTimeoutSec=" + socketTimeoutSec +
          ", basicDestination='" + basicDestination + '\'' +
          ", enableOfflineMode=" + (enableOfflineMode ? 1 : 0) +
          ", offlineCacheDir='" + offlineCacheDir + '\'' +
          ", offlineMessagesLoopSleepSec=" + offlineMessagesLoopSleepSec +
          ", offlineMessagesDisableThread=" + (offlineMessagesDisableThread ? 1 : 0) +
          ", defaultReceiptTimeoutSec=" + defaultReceiptTimeoutSec +
          '}';
    }
  }
}
