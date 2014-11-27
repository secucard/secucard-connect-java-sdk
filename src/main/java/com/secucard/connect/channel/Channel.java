package com.secucard.connect.channel;

import com.secucard.connect.event.EventListener;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.transport.QueryParams;

import java.io.IOException;

/**
 * Basic interface for communication with the secucard server.
 */
public interface Channel {

  /**
   * @throws IOException
   */
  void open() throws IOException;

  <T> T getObject(Class<T> type, String objectId);

  <T> ObjectList<T> findObjects(Class<T> type, QueryParams queryParams);

  <T extends SecuObject> T saveObject(T object);

  boolean deleteObject(Class type, String objectId);

  void setEventListener(EventListener listener);

  /**
   * Executing the given action.
   *
   * @param action
   * @param id
   * @param arg
   * @param returnType
   * @param <A>
   * @param <R>
   * @return
   */
  <A, R> R execute(String action, String[] id, A arg, Class<R> returnType);

  /**
   * Invoke any command.
   *
   * @param command
   * @param requestReceipt
   */
  void invoke(String command, boolean requestReceipt);

  void close();
}
