package com.secucard.connect.channel;

import com.secucard.connect.Callback;

import java.util.logging.Logger;

public abstract class AbstractChannel implements Channel {
  protected PathResolver pathResolver = new PathResolver();

  protected final Logger LOG = Logger.getLogger(getClass().getName());


  protected void onFailed(Callback callback, Throwable e) {
    if (callback != null) {
      try {
        callback.failed(e);
      } catch (Exception e1) {
        // ignore
      }
    }
  }

  protected <T> void onCompleted(Callback<T> callback, T result) {
    if (callback != null) {
      try {
        callback.completed(result);
      } catch (Exception e) {
        // ignore
      }
    }
  }
}
