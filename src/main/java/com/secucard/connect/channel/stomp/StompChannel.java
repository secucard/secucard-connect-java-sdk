package com.secucard.connect.channel.stomp;

import com.secucard.connect.Callback;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.transport.Message;
import com.secucard.connect.model.transport.QueryParams;
import com.secucard.connect.model.transport.Result;
import com.secucard.connect.util.CallbackAdapter;
import com.secucard.connect.util.Converter;

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
    Converter<Result, String> converter = new Converter<Result, String>() {
      @Override
      public String convert(Result value) {
        if (value == null) {
          return null;
        }
        return value.getResult();
      }
    };

    Callback<Result> adaptor = null;
    if (callback != null) {
      adaptor = new CallbackAdapter<>(callback, converter);
    }

    Result result = sendMessage(new StandardDestination(command), null,
        new MessageTypeRef(Result.class), adaptor, false);

    return converter.convert(result);
  }

  @Override
  public <T> T getObject(final Class<T> type, String objectId, final Callback<T> callback) {
    return sendMessage(new StandardDestination(StandardDestination.GET, type), new Message<T>(objectId),
        new MessageTypeRef(type), callback, true);
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
        statusHandler, callback, true);
  }

  @Override
  public <T> T createObject(T object, Callback<T> callback) {
    return sendMessage(new StandardDestination(StandardDestination.ADD, object.getClass()),
        new Message<>(object), new MessageTypeRef(object.getClass()), callback, true);
  }

  @Override
  public <T extends SecuObject> T updateObject(T object, Callback<T> callback) {
    return sendMessage(new StandardDestination(StandardDestination.UPDATE, object.getClass()),
        new Message<>(object.getId(), object), new MessageTypeRef(object.getClass()), callback, true);
  }

  @Override
  public <T> T updateObject(Class type, String objectId, String action, String actionArg, Object arg,
                            Class<T> returnType, Callback<T> callback) {
    return sendMessage(new StandardDestination(StandardDestination.UPDATE, type, action),
        new Message<>(objectId, actionArg, arg), new MessageTypeRef(returnType), callback, true);
  }

  @Override
  public void deleteObject(Class type, String objectId, Callback callback) {
    sendMessage(new StandardDestination(StandardDestination.DELETE, type), new Message<>(objectId),
        new MessageTypeRef(type), callback, true);
  }

  @Override
  public void deleteObject(Class type, String objectId, String action, String actionArg, Callback<?> callback) {
    sendMessage(new StandardDestination(StandardDestination.DELETE, type, action), new Message<>(objectId, actionArg),
        new MessageTypeRef(type), callback, true);
  }

  @Override
  public <T> T execute(String appId, String action, Object arg, Class<T> returnType, Callback<T> callback) {
    return sendMessage(new AppDestination(appId, action), arg, new MessageTypeRef(returnType), callback, true);
  }

  @Override
  public <T> T execute(Class type, String objectId, String action, String actionArg, Object arg, Class<T> returnType,
                       Callback<T> callback) {
    return sendMessage(new StandardDestination(StandardDestination.EXEC, arg.getClass(), action),
        new Message<>(objectId, actionArg, arg), new MessageTypeRef(returnType), callback, true);
  }
}
