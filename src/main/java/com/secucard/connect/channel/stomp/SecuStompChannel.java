package com.secucard.connect.channel.stomp;

import com.secucard.connect.Callback;
import com.secucard.connect.SecuException;
import com.secucard.connect.auth.AuthProvider;
import com.secucard.connect.client.ConnectionException;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.event.Events;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.general.Event;
import com.secucard.connect.model.transport.InvocationResult;
import com.secucard.connect.model.transport.Message;
import com.secucard.connect.model.transport.QueryParams;
import net.jstomplite.Config;
import net.jstomplite.StompClient;
import net.jstomplite.StompEventListener;
import net.jstomplite.StompException;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Map;

public class SecuStompChannel extends StompChannelBase implements StompEventListener {
  protected final StompClient stompClient;


  public SecuStompChannel(String id, Configuration cfg) {
    super(id, cfg);
    Config stompCfg = new Config(cfg.getHost(), cfg.getPort(), cfg.getVirtualHost(), cfg.getUserId(),
        cfg.getPassword(), cfg.getHeartbeatMs(), cfg.useSsl(), cfg.getSocketTimeoutSec(),
        cfg.getMessageTimeoutSec(), cfg.getConnectionTimeoutSec());
    this.stompClient = new StompClient(id, stompCfg, this);
  }

  @Override
  public String invoke(String command, Callback<String> callback) {
    invokeDefault(command, null, null, null, InvocationResult.class, false);
    return null;
  }

  @Override
  public <T> T getObject(Class<T> type, String objectId, Callback<T> callback) {
    return invokeDefault(API_GET, null, new Message(objectId), type, type, true);
  }

  @Override
  public <T> ObjectList<T> findObjects(Class<T> type, QueryParams queryParams, Callback<ObjectList<T>> callback) {
    Message<T> message = new Message<>();
    message.setQuery(queryParams);
    Message<ObjectList<T>> answer = invokeList(API_GET, null, message, type, type, true);
    if ("ProductNotFoundException".equalsIgnoreCase(answer.getError())) {
      return null;
    } else if (STATUS_OK.equalsIgnoreCase(answer.getStatus())) {
      return answer.getData();
    } else {
      throw new SecuException(answer.getError() + ", " + answer.getErrorDetails());
    }
  }

  @Override
  public <T extends SecuObject> T saveObject(T object, Callback<T> callback) {
    String command = object.getId() == null ? API_ADD : API_UPDATE;
    SecuObject secuObject = invokeDefault(command, null, new Message<>(object.getId(), object), object.getClass(),
        object.getClass(), true);
    return (T) secuObject;

  }

  @Override
  public void deleteObject(Class type, String objectId, Callback callback) {
    invoke(API_DELETE, null, new Message(objectId), type, null, true);
  }

  @Override
  public <T> T execute(String action, String resourceId, String strArg, Object arg, Class<T> returnType, Callback<T> callback) {
    Message message = new Message();
    message.setData(arg);
    if (StringUtils.isNotBlank(resourceId)) {
      message.setPid(resourceId);
    }
    if (StringUtils.isNotBlank(strArg)) {
      message.setSid(strArg);
    }

    return invokeDefault(API_EXEC, action, message, arg.getClass(), returnType, true);
  }

  private <T> T invokeDefault(String command, String action, Message arg, Class argType, Class<T> returnType,
                              boolean requestReceipt) {
    Message<T> answer = invoke(command, action, arg, argType, returnType, requestReceipt);
    if (!STATUS_OK.equalsIgnoreCase(answer.getStatus())) {
      throw new SecuException(answer.getError() + ", " + answer.getErrorDetails());
    }
    return answer.getData();
  }

  private <T> Message<T> invoke(String command, String action, Message arg, Class argType, Class returnType,
                                boolean requestReceipt) {
    String body = sendAndReceive(arg, resolveDestination(argType, command, action), requestReceipt);
    try {
      return bodyMapper.toMessage(returnType, body);
    } catch (IOException e) {
      throw new SecuException("Error converting from JSON.", e);
    }
  }

  private <T> Message<ObjectList<T>> invokeList(String command, String action, Message arg, Class argType,
                                                Class returnType, boolean requestReceipt) {
    String answer = sendAndReceive(arg, resolveDestination(argType, command, action), requestReceipt);
    try {
      return bodyMapper.toMessageList(returnType, answer);
    } catch (IOException e) {
      throw new SecuException("Error converting from JSON.", e);
    }
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
  public synchronized void open(Callback callback) throws IOException {
    clientOpen();
  }

  @Override
  public synchronized void close(Callback callback) {
    stompClient.close();
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
    } catch (net.jstomplite.NoReceiptException e) {
      e.printStackTrace();
    }

    body = awaitAnswer(id);
    if (body == null) {
      throw new MessageTimeoutException();
    }
    // todo: better catch and close client?

    return body;
  }

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
}
