package com.secucard.connect.channel;

import com.secucard.connect.Callback;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.transport.QueryParams;

import java.io.IOException;

/**
 * Basic interface for communication with the secucard server.
 */
public interface Channel {

  void open(Callback<?> callback) throws IOException;

  void setEventListener(EventListener listener);

  String invoke(String command, Callback<String> callback);

  <T> T getObject(Class<T> type, String objectId, Callback<T> callback);

  <T> ObjectList<T> findObjects(Class<T> type, QueryParams queryParams, Callback<ObjectList<T>> callback);

  <T extends SecuObject> T saveObject(T object, Callback<T> callback);

  void deleteObject(Class type, String objectId, Callback<?> callback);

  <T> T execute(String action, String resourceId, String strArg, Object arg, Class<T> returnType, Callback<T> callback);

  void close(Callback<?> callback);

}
