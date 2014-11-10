package com.secucard.connect.channel;

import com.secucard.connect.QueryParams;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.SecuObject;

import java.io.IOException;

/**
 * The channel the client communicates with the secucard server.
 */
public interface Channel {

  void open() throws IOException;

  <T extends SecuObject> T getObject(Class<T> type, String objectId);

  <T extends SecuObject> ObjectList<T> findObjects(Class<T> type, QueryParams q);

  <T extends SecuObject> T saveObject(T object);

  <T extends SecuObject> boolean deleteObject(Class<T> type, String objectId);

  void setEventListener(EventListener listener) throws UnsupportedOperationException;

  <A, R> R execute(String action, String[] id, A arg, Class<R> returnType);

  void invoke(String command);

  void close();
}
