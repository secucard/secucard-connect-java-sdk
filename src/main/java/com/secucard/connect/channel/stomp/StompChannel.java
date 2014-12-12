package com.secucard.connect.channel.stomp;

import com.secucard.connect.Callback;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.transport.InvocationResult;
import com.secucard.connect.model.transport.Message;
import com.secucard.connect.model.transport.QueryParams;
import com.secucard.connect.util.CallbackAdapter;
import com.secucard.connect.util.Converter;
import com.secucard.connect.util.jackson.DynamicTypeReference;
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
    Converter<InvocationResult, String> converter = new Converter<InvocationResult, String>() {
      @Override
      public String convert(InvocationResult value) {
        if (value == null) {
          return null;
        }
        return value.getResult();
      }
    };

    Callback<InvocationResult> adaptor = null;
    if (callback != null) {
      adaptor = new CallbackAdapter<>(callback, converter);
    }

    InvocationResult result = sendMessage(new StandardDestination(command), null,
        new MessageTypeRef(InvocationResult.class), adaptor, false);

    return converter.convert(result);
  }

  public <T> T invoke(String appId, String method, Object arg, Class<T> returnType, Callback<T> callback) {
    return sendMessage(new AppDestination(appId, method), arg, new MessageTypeRef(returnType), callback, true);
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
  public <T extends SecuObject> T saveObject(T object, Callback<T> callback) {
    String command = object.getId() == null ? StandardDestination.ADD : StandardDestination.UPDATE;
    return sendMessage(new StandardDestination(command, object.getClass()), new Message<>(object.getId(), object),
        new MessageTypeRef(object.getClass()), callback, true);
  }

  @Override
  public void deleteObject(Class type, String objectId, Callback callback) {
    sendMessage(new StandardDestination(StandardDestination.DELETE, type), new Message<>(objectId),
        new MessageTypeRef(type), callback, true);
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
    return sendMessage(new StandardDestination(StandardDestination.EXEC, arg.getClass(), action), message,
        new MessageTypeRef(returnType), callback, true);
  }
}
