package com.secucard.connect.stomp;

import com.secucard.connect.AbstractChannel;
import com.secucard.connect.EventListener;
import com.secucard.connect.QueryParams;
import com.secucard.connect.SecuException;
import com.secucard.connect.auth.AuthProvider;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.general.Event;
import com.secucard.connect.model.transport.Message;
import net.jstomplite.Config;
import net.jstomplite.StompClient;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

public class SecuStompChannel extends AbstractChannel {

  public static final String HEADER_CORRELATION_ID = "correlation-id";
  public static final String HEADER_SESSION = "session";
  public static final String STATUS_OK = "ok";

  public static final String API_GET = "api:get:";
  public static final String API_UPDATE = "api:update:";
  public static final String API_ADD = "api:add:";
  public static final String API_DELETE = "api:delete:";
  public static final String API_EXEC = "api:exec:";

  private MyStompClient stompClient;
  private BodyMapper bodyMapper;
  private Map<String, StompMessage> messages;
  private boolean connected;
  private AtomicReference<String> session = new AtomicReference<>(null);
  private EventListener eventListener;

  private AuthProvider authProvider;

  // settings
  private final StompConfig cfg;

  public SecuStompChannel(StompConfig cfg) {
    this.cfg = cfg;
    messages = new ConcurrentHashMap<>(50, 0.75f, 2);
    stompClient = new MyStompClient(new Config(cfg.getHost(), cfg.getPort(), cfg.getVirtualHost(), cfg.getUserId(),
        cfg.getPassword(), cfg.getHeartbeatMs(), cfg.isUseSsl(), cfg.getSocketTimeoutSec()));
  }

  public void setEventListener(EventListener eventListener) {
    this.eventListener = eventListener;
  }

  public void setAuthProvider(AuthProvider authProvider) {
    this.authProvider = authProvider;
  }

  public void setBodyMapper(BodyMapper bodyMapper) {
    this.bodyMapper = bodyMapper;
  }

  @Override
  public void open() throws IOException {
    session.set(null);
    connected = false;

    // todo: connecting with the configured credentials for now, switch!
    stompClient.connect();
//    stompClient.connect(authProvider.getToken().getAccessToken(), "");

    if (!awaitConnected()) {
      throw new IOException("Not connected");
    }
  }

  @Override
  public <T extends SecuObject> T getObject(Class<T> type, String objectId) {
    String destination = resolveDestination(type, API_GET, null);
    String body = sendAndReceive(new Message<T>(objectId), destination);
    Message<T> answer = readAnswer(type, body);

    if ("ProductNotFoundException".equalsIgnoreCase(answer.getError())) {
      return null;
    } else if (STATUS_OK.equalsIgnoreCase(answer.getStatus())) {
      return answer.getData();
    } else {
      throw new SecuException(answer.getError() + ", " + answer.getErrorDetails());
    }
  }

  @Override
  public <T extends SecuObject> ObjectList<T> findObjects(Class<T> type, QueryParams q) {
    String destination = resolveDestination(type, API_GET, null);
    String body = sendAndReceive(null, destination);
    Message<ObjectList<T>> answer = readAnswerList(type, body);
    if ("ProductNotFoundException".equalsIgnoreCase(answer.getError())) {
      return null;
    } else if (STATUS_OK.equalsIgnoreCase(answer.getStatus())) {
      return answer.getData();
    } else {
      throw new SecuException(answer.getError() + ", " + answer.getErrorDetails());
    }
  }

  @Override
  public <T extends SecuObject> T saveObject(T object) {
    String destination = resolveDestination(object.getClass(), object.getId() == null ? API_ADD : API_UPDATE, null);
    Message<T> message = new Message<>(object.getId(), object);
    String body = sendAndReceive(message, destination);
    Message<? extends SecuObject> answer = readAnswer(object.getClass(), body);
    if ("ProductNotFoundException".equalsIgnoreCase(answer.getError())) {
      return null;
    } else if (STATUS_OK.equalsIgnoreCase(answer.getStatus())) {
      return (T) answer.getData();
    } else {
      throw new SecuException(answer.getError() + ", " + answer.getErrorDetails());
    }
  }

  @Override
  public <T extends SecuObject> boolean deleteObject(Class<T> type, String objectId) {
    String destination = resolveDestination(type, API_DELETE, null);
    String body = sendAndReceive(new Message<T>(objectId), destination);
    Message answer = readAnswer(body);
    if ("ProductNotFoundException".equalsIgnoreCase(answer.getError())) {
      return false;
    } else if (STATUS_OK.equalsIgnoreCase(answer.getStatus())) {
      return true;
    } else {
      throw new SecuException(answer.getError() + ", " + answer.getErrorDetails());
    }
  }

  @Override
  public <A, R> R execute(String action, String[] ids, A arg, Class<R> returnType) {
    String destination = resolveDestination(arg.getClass(), API_EXEC, action);
    Message<A> message = new Message<>();
    message.setData(arg);
    if (ids != null && ids.length > 0) {
      message.setPid(ids[0]);
    }
    if (ids != null && ids.length > 1) {
      message.setSid(ids[1]);
    }

    String body = sendAndReceive(message, destination);

    Message<R> answer = null;
    if (returnType == null) {
      answer = readAnswer(body);
    } else {
      answer = readAnswer(returnType, body);
    }

    if ("ProductNotFoundException".equalsIgnoreCase(answer.getError())) {
      return null;
    } else if (STATUS_OK.equalsIgnoreCase(answer.getStatus())) {
      return returnType == null ? (R) Boolean.TRUE : answer.getData();
    } else {
      throw new SecuException(answer.getError() + ", " + answer.getErrorDetails());
    }
  }

  @Override
  public void close() {
    stompClient.close();
  }

  // private ----------------------------------------------------------------------------------------------------------

  private <T extends SecuObject> Message<ObjectList<T>> readAnswerList(Class<T> type, String body) {
    try {
      return bodyMapper.toMessageList(type, body);
    } catch (IOException e) {
      throw new SecuException("Error reading anser", e);
    }
  }

  private Message readAnswer(String body) {
    return readAnswer(Map.class, body);
  }

  private <T> Message<T> readAnswer(Class<T> type, String body) {
    try {
      return bodyMapper.toMessage(type, body);
    } catch (IOException e) {
      throw new SecuException("Error reading anser", e);
    }
  }

  private <T> String sendAndReceive(Message<T> message, String dest) {
    final String id = createCorrelationId();
    Map<String, String> headers = createDefaultHeaders(id);

    if (!awaitConnected()) {
      throw new SecuException("Not connected");
    }

    String body = null;
    try {
      body = null;
      if (message != null) {
        body = bodyMapper.map(message);
        headers.put("content-type", "application/json");
        headers.put("content-length", Integer.toString(body.getBytes("UTF-8").length));
      }

      stompClient.send(dest, body, headers);

      if (cfg.isUseReceipt() && awaitReceipt(id) == null) {
        // no receipt received (in time)
        throw new SecuException("No message receipt received or timeout before receiving");
      }

      body = awaitAnswer(id);
      if (body == null) {
        // no corresponding message found
        throw new SecuException("Got no answer to message.");
      }

    } catch (Exception e) {
      throw new SecuException("Error sending message", e);
    }
    return body;
  }


  private String createCorrelationId() {
    return Long.toString(System.currentTimeMillis());
  }

  private Map<String, String> createDefaultHeaders(String id) {
    Map<String, String> headers = StompClient.createHeader(
        "user-id", cfg.getUserId(),
        "reply-to", cfg.getReplyQueue(),
        "correlation-id", id,
        "persistent", "true"
    );
    if (cfg.isUseReceipt()) {
      headers.put("receipt", id);
    }
    return headers;
  }

  private boolean awaitConnected() {
    try {
      Boolean result = new ConnectionPollTask().get(cfg.getConnectionTimeoutSec());
      return result != null && result;
    } catch (ExecutionException e) {
      return false;
    }
  }

  private String awaitAnswer(String id) throws ExecutionException {
    return new MessagePollTask(id).get();
  }

  private String awaitReceipt(String id) throws ExecutionException {
    return awaitAnswer("rcpt#" + id);
  }

  private String pullMessage(String id) {
    long t = System.currentTimeMillis();
    Iterator<Map.Entry<String, StompMessage>> it = messages.entrySet().iterator();
    while (it.hasNext()) {
      StompMessage message = it.next().getValue();
      long ageSec = (t - message.receiveTime) / 1000;
      boolean match = message.id.equalsIgnoreCase(id);
      if (match || ageSec > cfg.getMaxMessageAgeSec()) {
        it.remove();
        if (match) {
          return message.body == null ? "" : message.body;
        }
      }
    }
    return null;
  }

  private class MyStompClient extends StompClient {
    private MyStompClient(Config config) {
      super(config);
    }

    @Override
    protected void onConnect(Map<String, String> headers) {
      String value = headers.get(HEADER_SESSION);
      if (value == null || value.isEmpty()) {
        close();
        throw new IllegalStateException("Protocol error, session id not found");
      }
      session.set(value);
      connected = true;
    }

    @Override
    protected void onReceipt(String id) {
      putMessage("rcpt#" + id, null);
    }

    @Override
    protected void onMessage(String messageId, String subscription, String destination, Map<String, String> headers,
                             String body) {
      // todo handle other messages like errors, test for destination and so on
      String correlationId = headers.get(HEADER_CORRELATION_ID);
      if (correlationId != null) {
        putMessage(correlationId, body);
      }
      if (correlationId == null && body.contains("CashierDisplay")) {
        eventListener.onEvent(new Event(body));
      }
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

    @Override
    protected void onError(Map<String, String> headers) {
      // todo: handle "Bad CONNECT" message, indicates that stomp logical connection is not set up
      eventListener.onEvent(new Event(headers.toString()));
    }

    @Override
    protected void onDisconnect(Exception ex) {
      connected = false;
    }
  }

  private <T> String resolveDestination(Class<T> type, String method, String action) {
    String path = cfg.getBaseDestination() + method + pathResolver.resolve(type, '.');
    if (action != null) {
      path += "." + action;
    }
    return path;
  }

  private class StompMessage {
    public String id;
    public String body;
    public long receiveTime;

    private StompMessage(String id, String body) {
      this.id = id;
      this.body = body;
      receiveTime = System.currentTimeMillis();
    }
  }

  private class MessagePollTask extends AbstractPollTask<String> {
    private final String id;

    public MessagePollTask(String id) {
      this.id = id;
    }

    public String get() throws ExecutionException {
      return super.get(cfg.getMessagePollTimeoutSec());
    }

    @Override
    protected String call() {
      do {
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          break;
        }
        String msg = pullMessage(id);
        if (msg != null) {
          return msg;
        }
      } while (!Thread.currentThread().isInterrupted());
      return null;
    }
  }

  private class ConnectionPollTask extends AbstractPollTask<Boolean> {
    @Override
    protected Boolean call() {
      do {
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          break;
        }
        if (connected) {
          return Boolean.TRUE;
        }
      } while (!Thread.currentThread().isInterrupted());
      return Boolean.FALSE;
    }
  }
}
