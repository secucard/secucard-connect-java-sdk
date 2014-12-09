package com.secucard.connect.channel.stomp;

import com.secucard.connect.Callback;
import com.secucard.connect.SecuException;
import com.secucard.connect.auth.AuthProvider;
import com.secucard.connect.channel.AbstractChannel;
import com.secucard.connect.ConnectionException;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.event.Events;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.auth.Token;
import com.secucard.connect.model.general.Event;
import com.secucard.connect.model.transport.Message;
import net.jstomplite.Config;
import net.jstomplite.StompClient;
import net.jstomplite.Frame;
import net.jstomplite.StompSupport;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class StompChannelBase extends AbstractChannel {
  protected static final String HEADER_CORRELATION_ID = "correlation-id";
  protected static final String STATUS_OK = "ok";

  protected static final String API_GET = "api:get:";
  protected static final String API_UPDATE = "api:update:";
  protected static final String API_ADD = "api:add:";
  protected static final String API_DELETE = "api:delete:";
  protected static final String API_EXEC = "api:exec:";

  protected BodyMapper bodyMapper;
  private final Map<String, Frame> receipts = new HashMap<>(20);
  protected final Map<String, StompMessage> messages = new HashMap<>(20);
  private final Object monitor = new Object();
  protected EventListener eventListener;
  protected AuthProvider authProvider;
  private final String id;
  protected final Configuration configuration;
  protected volatile boolean connected;
  protected final StompSupport stompSupport;
  private final StatusHandler defaultStatusHandler = new StatusHandler() {
    @Override
    public boolean hasError(Message message) {
      return !message.getStatus().equalsIgnoreCase(STATUS_OK);
    }
  };

  public void setEventListener(EventListener eventListener) {
    this.eventListener = eventListener;
  }

  public void setAuthProvider(AuthProvider authProvider) {
    this.authProvider = authProvider;
  }

  public void setBodyMapper(BodyMapper bodyMapper) {
    this.bodyMapper = bodyMapper;
  }

  public StompChannelBase(String id, Configuration cfg) {
    this.configuration = cfg;
    this.id = id;
    Config stompCfg = new Config(cfg.getHost(), cfg.getPort(), cfg.getVirtualHost(), cfg.getUserId(),
        cfg.getPassword(), cfg.getHeartbeatMs(), cfg.useSsl(), cfg.getSocketTimeoutSec(),
        cfg.getMessageTimeoutSec(), cfg.getConnectionTimeoutSec());
    stompSupport = new StompSupport(id, stompCfg, new DefaultEventListner());
  }

  /**
   * Returns if a stomp error is a connection related error.
   */
  protected static boolean isConnectionError(Map<String, String> headers, String body) {
    if (headers != null && headers.containsKey("message")) {
      if (headers.get("message").contains("Bad CONNECT")) {
        return true;
      }
    }
    return true;
  }

  void putMessage(String id, String body) {
    if (id != null && !id.isEmpty()) {
      StompMessage msg = new StompMessage(id, body);
      StompMessage m = messages.put(msg.id, msg);
      if (m != null) {
        // ignore for now
      }
    }
  }

  String pullMessage(String id) {
    long t = System.currentTimeMillis();
    Iterator<Map.Entry<String, StompMessage>> it = messages.entrySet().iterator();
    while (it.hasNext()) {
      StompMessage message = it.next().getValue();
      long ageSec = (t - message.receiveTime) / 1000;
      boolean match = message.id.equalsIgnoreCase(id);
      if (match || ageSec > configuration.getMaxMessageAgeSec()) {
        it.remove();
        if (match) {
          return message.body == null ? "" : message.body;
        }
      }
    }
    return null;
  }


  String createCorrelationId() {
    return (id == null ? Integer.toString(hashCode()) : id) + "-" + System.currentTimeMillis();
  }


  Map<String, String> createDefaultHeaders(String id) {
    Map<String, String> header = StompClient.createHeader(
        "reply-to", configuration.getReplyQueue(),
        "correlation-id", id,
        "persistent", "true"
    );

    String userId = configuration.getUserId();
    if (authProvider != null) {
      Token token = authProvider.getToken();
      if (token != null) {
        userId = token.getAccessToken();
      }
    }

    if (userId != null) {
      header.put("user-id", userId);
    }

    return header;
  }

  String resolveDestination(Class type, String method, String action) {
    String dest = configuration.getBasicDestination();
    if (!dest.endsWith("/")) {
      dest = dest + "/";
    }

    String path = dest + method;

    if (type != null) {
      path += pathResolver.resolve(type, '.');
    }

    if (action != null) {
      path += "." + action;
    }

    return path;
  }


  void awaitReceipt(final String receipt, final Callback<?> callback) {
    // todo: cleanup old receipts

    if (receipt == null) {
      if (callback != null) {
        onCompleted(callback, null);
      }
      return;
    }

    long maxWaitTime = System.currentTimeMillis() + configuration.getMessageTimeoutSec() * 1000;
    Frame frm;
    synchronized (receipts) {
      while (System.currentTimeMillis() <= maxWaitTime) {
        if (receipts.containsKey(receipt)) {
          break;
        }
        try {
          receipts.wait(1000);
        } catch (InterruptedException e) {
          // will be stopped anyway
        }
      }
      frm = receipts.remove(receipt);
    }

    NoReceiptException exception = null;
    if (frm == null) {
      exception = new NoReceiptException("No receipt (" + receipt + ") received in time.");
    } else if (StompSupport.ERROR.equals(frm.getCommand())) {
      exception = new NoReceiptException(frm.toString());
    }

    if (callback == null) {
      if (exception != null) {
        throw exception;
      }
    } else if (exception == null) {
      onCompleted(callback, null);
    } else {
      onFailed(callback, exception);
    }

  }

  protected void awaitConnection(Callback callback) {
    long maxWaitTime = System.currentTimeMillis() + configuration.getConnectionTimeoutSec() * 1000;
    synchronized (monitor) {
      while (System.currentTimeMillis() <= maxWaitTime) {
        if (connected) {
          break;
        }
        try {
          monitor.wait(1000);
        } catch (InterruptedException e) {
          // will be stopped anyway
        }
      }
    }

    if (callback == null) {
      if (!connected) {
        throw new ConnectionTimeoutException();
      }
    } else if (connected) {
      onCompleted(callback, null);
    } else {
      onFailed(callback, new ConnectionTimeoutException());
    }
  }

  String awaitAnswer(final String id, Callback<String> callback) {
    long maxWaitTime = System.currentTimeMillis() + configuration.getMessageTimeoutSec() * 1000;
    String msg;
    synchronized (messages) {
      while (System.currentTimeMillis() <= maxWaitTime) {
        if (messages.containsKey(id)) {
          break;
        }
        try {
          messages.wait(1000);
        } catch (InterruptedException e) {
          // will be stopped anyway
        }
      }
      msg = pullMessage(id);
    }

    RuntimeException exception = null;
    if (msg == null) {
      exception = new MessageTimeoutException("No answer for " + id + " received in time");
    }

    if (callback == null) {
      if (exception != null) {
        throw exception;
      }
    } else if (exception == null) {
      onCompleted(callback, msg);
    } else {
      onFailed(callback, exception);
    }

    return msg;
  }

  protected <T> T sendMessage(String command, String action, Message message, Class type, Mapper<T> mapper,
                              Callback<T> callback) {
    return sendMessage(command, action, message, type, mapper, defaultStatusHandler, callback, true);
  }

  protected synchronized <T> T sendMessage(String command, String action, Message message, Class type,
                                           final Mapper<T> mapper, final StatusHandler statusHandler,
                                           final Callback<T> callback, boolean requestReceipt) {
    final String corrId = createCorrelationId();
    Map<String, String> headers = createDefaultHeaders(corrId);
    String body;
    try {
      body = bodyMapper.map(message);
      headers.put("content-type", "application/json");
      headers.put("content-length", Integer.toString(body.getBytes("UTF-8").length));
    } catch (IOException e) {
      SecuException exception = new SecuException("Error converting to JSON.", e);
      if (callback == null) {
        throw exception;
      } else {
        onFailed(callback, exception);
        return null;
      }
    }

    if (!connected) {
      try {
        open(null);
      } catch (Exception e) {
        ConnectionException connectionException = new ConnectionException(e);
        if (callback == null) {
          throw connectionException;
        } else {
          onFailed(callback, connectionException);
          return null;
        }
      }
    }

    String destination = resolveDestination(type, command, action);
    final String receipt;
    try {
      receipt = stompSupport.send(destination, body, headers, requestReceipt);
    } catch (Exception e) {
      close(null);
      SecuException exception = new SecuException("Error sending stomp message.", e);
      if (callback != null) {
        onFailed(callback, exception);
        return null;
      }
      throw exception;
    }

    T result = null;
    if (callback == null) {
      awaitReceipt(receipt, null);
      final String answer = awaitAnswer(corrId, null);
      try {
        Message<T> msg = mapper.map(answer);
        if (msg != null) {
          statusHandler.check(msg);
          result = msg.getData();
        }
      } catch (Exception e) {
        throw new SecuException("Error reading response message.", e);
      }
    } else {
      new AsyncExecution() {
        @Override
        protected void run() {
          awaitReceipt(receipt, new Callback() {
            @Override
            public void completed(Object result) {
              // get answer here, no need for async call since this executed in another thread anyway
              T data;
              try {
                Message<T> msg = mapper.map(awaitAnswer(corrId, null));
                statusHandler.check(msg);
                data = msg.getData();
              } catch (Exception e) {
                onFailed(callback, e);
                return;
              }
              onCompleted(callback, data);
            }

            @Override
            public void failed(Throwable throwable) {
              onFailed(callback, throwable);
            }
          });
        }
      }.start();
    }

    return result;
  }



  // Inner Classes -----------------------------------------------------------------------------------------------------

  private class StompMessage {
    public final String id;
    public final String body;
    public final long receiveTime;

    private StompMessage(String id, String body) {
      this.id = id;
      this.body = body;
      receiveTime = System.currentTimeMillis();
    }
  }

  interface Mapper<T> {
    public Message<T> map(String value) throws Exception;
  }

  protected abstract class StatusHandler {
    public abstract boolean hasError(Message message);

    public void check(Message message) {
      if (hasError(message)) {
        throw new SecuException(message.getError() + ", " + message.getErrorDetails());
      }
    }
  }

  protected class ObjectListMapper<T> implements Mapper<ObjectList<T>> {
    private final Class<T> type;

    public ObjectListMapper(Class<T> type) {
      this.type = type;
    }

    @Override
    public Message<ObjectList<T>> map(String value) throws Exception {
      return bodyMapper.toMessageList(type, value);
    }
  }

  protected class ObjectMapper<T> implements Mapper<T> {
    private final Class<T> type;

    public ObjectMapper(Class<T> type) {
      this.type = type;
    }

    @Override
    public Message<T> map(String value) throws Exception {
      return bodyMapper.toMessage(type, value);
    }
  }

  // Default Stomp Message Handling ------------------------------------------------------------------------------------

  private class DefaultEventListner implements net.jstomplite.EventListener {
    @Override
    public void onConnect() {
      synchronized (monitor) {
        connected = true;
        monitor.notify();
      }
      if (eventListener != null) {
        eventListener.onEvent(Events.CONNECTED);
      }
    }

    @Override
    public void onReceipt(String receipt) {
      synchronized (receipts) {
        receipts.put(receipt, new Frame(StompSupport.RECEIPT));
        receipts.notify();
      }
    }

    @Override
    public void onMessage(Frame frame) {
      String correlationId = frame.getHeaders().get(HEADER_CORRELATION_ID);
      String body = frame.getBody();
      if (correlationId != null) {
        synchronized (messages) {
          putMessage(correlationId, body);
          messages.notify();
        }
      }

      if (eventListener != null && correlationId == null && body.contains("CashierDisplay")) {
        eventListener.onEvent(new Event(body));
      }
    }

    @Override
    public void onError(Frame frame) {
      String receiptId = frame.getHeaders() == null ? null : frame.getHeaders().get("receipt-id");
      if (receiptId != null) {
        // this is an error instead regular receipt
        synchronized (receipts) {
          receipts.put(receiptId, frame);
          receipts.notify();
        }
      }

      if (isConnectionError(frame.getHeaders(), frame.getBody())) {
        synchronized (monitor) {
          connected = false;
          monitor.notify();
        }
      }

      if (eventListener != null) {
        eventListener.onEvent(frame.toString());
      }
    }

    @Override
    public void onDisconnect() {
      synchronized (monitor) {
        connected = false;
        monitor.notify();
      }
      if (eventListener != null) {
        eventListener.onEvent(Events.DISCONNECTED);
      }
    }
  }
}
