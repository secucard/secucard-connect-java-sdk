package com.secucard.connect.channel.stomp;

import com.secucard.connect.SecuException;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.transport.InvocationResult;
import com.secucard.connect.model.transport.Message;
import com.secucard.connect.model.transport.QueryParams;

import java.io.IOException;

public class SecuStompChannel extends StompChannelBase {

  public SecuStompChannel(String id, Configuration cfg) {
    super(id, cfg);
  }

  @Override
  public void invoke(String command, boolean requestReceipt) {
    invokeDefault(command, null, null, null, InvocationResult.class, requestReceipt);
  }

  @Override
  public <T> T getObject(Class<T> type, String objectId) {
    return invokeDefault(API_GET, null, new Message(objectId), type, type, true);
  }

  @Override
  public <T> ObjectList<T> findObjects(Class<T> type, QueryParams q) {
    Message<ObjectList<T>> answer = invokeList(API_GET, null, null, type, type, true);
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
    String command = object.getId() == null ? API_ADD : API_UPDATE;
    SecuObject secuObject = invokeDefault(command, null, new Message<>(object.getId(), object), object.getClass(),
        object.getClass(), true);
    return (T) secuObject;

  }

  @Override
  public boolean deleteObject(Class type, String objectId) {
    invoke(API_DELETE, null, new Message(objectId), type, null, true);
    return true;
  }

  @Override
  public <A, R> R execute(String action, String[] ids, A arg, Class<R> returnType) {
    Message<A> message = new Message<>();
    message.setData(arg);
    if (ids != null && ids.length > 0) {
      message.setPid(ids[0]);
    }
    if (ids != null && ids.length > 1) {
      message.setSid(ids[1]);
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
}
