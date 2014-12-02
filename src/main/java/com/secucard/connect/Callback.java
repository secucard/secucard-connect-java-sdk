package com.secucard.connect;

public interface Callback<T> {

  void completed(T result);

  void failed(Throwable throwable);
}
