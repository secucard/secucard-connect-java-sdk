package com.secucard.connect.channel;

import com.secucard.connect.Callback;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.QueryParams;

import java.io.IOException;

/**
 * Basic interface for communication with the secucard server.
 */
public interface Channel {

  void open(Callback<?> callback) throws IOException;

  void setEventListener(EventListener listener);

  /**
   * Invoking arbitrary commands and returning the response without any conversion as plain string.
   *
   * @param command  The command.
   * @param callback Callback for asynchronous response and error handling.
   * @return The response message if no callback is used, else null.
   */
  String invoke(String command, Callback<String> callback);

  <T> T getObject(Class<T> type, String objectId, Callback<T> callback);


  <T> ObjectList<T> findObjects(Class<T> type, QueryParams queryParams, Callback<ObjectList<T>> callback);


  <T> T createObject(T object, Callback<T> callback);


  <T extends SecuObject> T updateObject(T object, Callback<T> callback);

  <T> T updateObject(Class product, String objectId, String action, String actionArg, Object arg,
                     Class<T> returnType, Callback<T> callback);


  void deleteObject(Class type, String objectId, Callback<?> callback);

  void deleteObject(Class product, String objectId, String action, String actionArg, Callback<?> callback);


  <T> T execute(Class product, String objectId, String action, String actionArg, Object arg, Class<T> returnType,
                Callback<T> callback);

  <T> T execute(String appId, String action, Object arg, Class<T> returnType, Callback<T> callback);


  void close(Callback<?> callback);

}
