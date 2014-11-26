package com.secucard.connect.channel.stomp;

import com.secucard.connect.SecuException;
import com.secucard.connect.auth.AuthProvider;
import com.secucard.connect.channel.AbstractChannel;
import com.secucard.connect.client.ConnectionException;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.event.Events;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.general.Event;
import com.secucard.connect.model.transport.Message;
import net.jstomplite.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class StompChannelBase extends AbstractChannel implements StompEventListener {
  protected static final String HEADER_CORRELATION_ID = "correlation-id";
  protected static final String STATUS_OK = "ok";

  protected static final String API_GET = "api:get:";
  protected static final String API_UPDATE = "api:update:";
  protected static final String API_ADD = "api:add:";
  protected static final String API_DELETE = "api:delete:";
  protected static final String API_EXEC = "api:exec:";

  protected BodyMapper bodyMapper;
  protected EventListener eventListener;
  protected AuthProvider authProvider;
  protected final String id;
  protected final StompClient stompClient;
  private final Map<String, StompMessage> messages = new HashMap<>(20);
  protected final Configuration configuration;

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
    stompClient = new StompClient(id, stompCfg, this);
  }

  public synchronized void close() {
    stompClient.close();
  }

  public synchronized void open() {
    clientOpen();
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


  protected String awaitAnswer(final String id) {
    long maxWaitTime = System.currentTimeMillis() + configuration.getMessageTimeoutSec() * 1000;
    synchronized (messages) {
      while (System.currentTimeMillis() <= maxWaitTime) {
        String msg = pullMessage(id);
        if (msg != null) {
          return msg;
        }
        try {
          messages.wait(1000);
        } catch (InterruptedException e) {
          return pullMessage(id);
        }
      }
    }
    return null;
  }

  private void putMessage(String id, String body) {
    if (id != null && !id.isEmpty()) {
      StompMessage msg = new StompMessage(id, body);
      StompMessage m = messages.put(msg.id, msg);
      if (m != null) {
        // ignore for now
      }
    }
  }

  private String pullMessage(String id) {
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


  private String createCorrelationId() {
    return (id == null ? Integer.toString(hashCode()) : id) + "-" + System.currentTimeMillis();
  }


  protected Map<String, String> createDefaultHeaders(String id) {
    return StompClient.createHeader(
        "user-id", authProvider.getToken().getAccessToken(),
        "reply-to", configuration.getReplyQueue(),
        "correlation-id", id,
        "persistent", "true"
    );
  }

  protected String resolveDestination(Class type, String method, String action) {
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

  private void clientOpen() {
    String login = configuration.getUserId();
    String pwd = configuration.getPassword();
    if (login == null || pwd == null) {
      String token = authProvider.getToken().getAccessToken();
      login = pwd = token;
    }

    try {
      stompClient.open(login, pwd);
    } catch (Exception e) {
      stompClient.close();
      throw new ConnectionException(e);
    }
  }

  protected synchronized String sendAndReceive(Message message, String dest, boolean requestReceipt) {
    final String id = createCorrelationId();
    Map<String, String> headers = createDefaultHeaders(id);
    String body = null;
    if (message != null) {
      try {
        body = bodyMapper.map(message);
        headers.put("content-type", "application/json");
        headers.put("content-length", Integer.toString(body.getBytes("UTF-8").length));
      } catch (IOException e) {
        throw new SecuException("Error converting to JSON.", e);
      }
    }

    clientOpen();

    try {
      stompClient.send(dest, body, headers, requestReceipt);
    } catch (IOException | NoReceiptException | StompException e) {
      stompClient.close();
      throw new SecuException("Error sending stomp message.", e);
    }

    body = awaitAnswer(id);
    if (body == null) {
      throw new MessageTimeoutException();
    }
    // todo: better catch and close client?

    return body;
  }


  // Default Stomp Message Handling ------------------------------------------------------------------------------------

  @Override
  public void onConnect() {
    if (eventListener != null) {
      eventListener.onEvent(Events.CONNECTED);
    }
  }

  @Override
  public void onDisconnect() {
    if (eventListener != null) {
      eventListener.onEvent(Events.DISCONNECTED);
    }
  }

  @Override
  public void onMessage(Map<String, String> headers, String body) {
    if (isConnectionError(headers, body)) {
      // todo: check out which message
    }

    String correlationId = headers.get(HEADER_CORRELATION_ID);
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
  public void onError(Map<String, String> headers, String body) {
    if (isConnectionError(headers, body)) {
      // todo: check out which errors
    }
    if (eventListener != null) {
      eventListener.onEvent(headers + " / " + body);
    }
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
}
