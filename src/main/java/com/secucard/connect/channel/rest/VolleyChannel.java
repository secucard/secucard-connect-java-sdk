package com.secucard.connect.channel.rest;

import com.secucard.connect.auth.AuthProvider;
import com.secucard.connect.channel.AbstractChannel;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.auth.Token;
import com.secucard.connect.model.transport.QueryParams;

import java.io.IOException;

public class VolleyChannel extends AbstractChannel implements AuthProvider {

  @Override
  public void open() throws IOException {

  }

  @Override
  public <T> T getObject(Class<T> type, String objectId) {
    return null;
  }

  @Override
  public <T> ObjectList<T> findObjects(Class<T> type, QueryParams queryParams) {
    return null;
  }

  @Override
  public <T extends SecuObject> T saveObject(T object) {
    return null;
  }

  @Override
  public boolean deleteObject(Class type, String objectId) {
    return false;
  }

  @Override
  public void setEventListener(EventListener listener) {

  }

  @Override
  public <A, R> R execute(String action, String[] id, A arg, Class<R> returnType) {
    return null;
  }

  @Override
  public void invoke(String command, boolean requestReceipt) {

  }

  @Override
  public void close() {

  }

  @Override
  public Token getToken() {
    return null;
  }
}
