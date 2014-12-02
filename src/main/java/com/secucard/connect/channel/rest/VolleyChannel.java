package com.secucard.connect.channel.rest;

import com.secucard.connect.Callback;
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
  public void open(Callback callback) throws IOException {

  }

  @Override
  public void close(Callback callback) {

  }

  @Override
  public void setEventListener(EventListener listener) {

  }

  @Override
  public String invoke(String command, Callback<String> callback) {
    return null;
  }

  @Override
  public <T> T getObject(Class<T> type, String objectId, Callback<T> callback) {
    return null;
  }

  @Override
  public <T> ObjectList<T> findObjects(Class<T> type, QueryParams queryParams, Callback<ObjectList<T>> callback) {
    return null;
  }

  @Override
  public <T extends SecuObject> T saveObject(T object, Callback<T> callback) {
    return null;
  }

  @Override
  public void deleteObject(Class type, String objectId, Callback callback) {

  }

  @Override
  public <T> T execute(String action, String resourceId, String strArg, Object arg, Class<T> returnType, Callback<T> callback) {
    return null;
  }

  @Override
  public Token getToken() {
    return null;
  }
}
