package com.secucard.connect.channel.stomp;

import com.secucard.connect.Callback;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.transport.InvocationResult;
import com.secucard.connect.model.transport.Message;
import com.secucard.connect.model.transport.QueryParams;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class StompChannel extends StompChannelBase {

  public StompChannel(String id, Configuration cfg) {
    super(id, cfg);
  }

  @Override
  public synchronized void open(final Callback callback) throws IOException {
    if (connected) {
      onCompleted(callback, null);
      return;
    }

    try {
      String login = configuration.getUserId();
      String pwd = configuration.getPassword();
      if (login == null || pwd == null) {
        String token = authProvider.getToken().getAccessToken();
        login = pwd = token;
      }
      stompSupport.open(login, pwd);
    } catch (Exception e) {
      if (callback == null) {
        throw e;
      } else {
        onFailed(callback, e);
        return;
      }
    }

    if (callback == null) {
      awaitConnection(null);
    } else {
      new AsyncExecution() {
        @Override
        protected void run() {
          awaitConnection(callback);
        }
      }.start();
    }
  }

  @Override
  public synchronized void close(Callback callback) {
    if (connected) {
      try {
        stompSupport.close();
        onCompleted(callback, null);
      } catch (Exception e) {
        if (callback == null) {
          throw e;
        }
        onFailed(callback, e);
      }
    }
  }

  @Override
  public String invoke(String command, final Callback<String> callback) {
    Callback<InvocationResult> adaptor = null;
    if (callback != null) {
      adaptor = new Callback<InvocationResult>() {
        @Override
        public void completed(InvocationResult result) {
          onCompleted(callback, result.getResult());
        }

        @Override
        public void failed(Throwable throwable) {
          onFailed(callback, throwable);
        }
      };
    }

    InvocationResult result = sendMessage(command, null, null, null, new ObjectMapper<>(InvocationResult.class),
        adaptor);
    return result.getResult();
  }

  @Override
  public <T> T getObject(final Class<T> type, String objectId, final Callback<T> callback) {
    return sendMessage(API_GET, null, new Message<T>(objectId), type, new ObjectMapper<>(type), callback);
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

    return sendMessage(API_GET, null, message, type, new ObjectListMapper<>(type), statusHandler, callback, true);
  }

  @Override
  public <T extends SecuObject> T saveObject(T object, Callback<T> callback) {
    String command = object.getId() == null ? API_ADD : API_UPDATE;
    ObjectMapper<T> mapper = new ObjectMapper<>((Class<T>) object.getClass());
    return sendMessage(command, null, new Message<>(object.getId(), object), object.getClass(), mapper, callback);
  }

  @Override
  public void deleteObject(Class type, String objectId, Callback callback) {
    sendMessage(API_DELETE, null, new Message<>(objectId), type, new ObjectMapper(type), callback);
  }

  @Override
  public <T> T execute(String action, String resourceId, String strArg, Object arg, Class<T> returnType,
                       Callback<T> callback) {
    Message message = new Message<>(null, arg);
    if (StringUtils.isNotBlank(resourceId)) {
      message.setPid(resourceId);
    }
    if (StringUtils.isNotBlank(strArg)) {
      message.setSid(strArg);
    }
    return sendMessage(API_EXEC, action, message, arg.getClass(), new ObjectMapper<>(returnType),
        callback);
  }
}
