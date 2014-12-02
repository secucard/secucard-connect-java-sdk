package com.secucard.connect;

public abstract class CallbackAdapter<T, A> implements Callback<T> {
  private Callback<A> callback;

  public CallbackAdapter(Callback<A> callback) {
    this.callback = callback;
  }

  @Override
  public void completed(T result) {
    try {
      A convert = null;
      if (result != null) {
        convert = convert(result);
      }
      callback.completed(convert);
    } catch (Exception e) {
      // log
    }
  }

  @Override
  public void failed(Throwable throwable) {
    try {
      callback.failed(throwable);
    } catch (Exception e) {
      // log
    }
  }

  protected abstract A convert(T object);
}
