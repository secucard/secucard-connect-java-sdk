package com.secucard.connect.util;


import com.secucard.connect.client.Callback;

public class CallbackAdapter<FROM, TO> implements Callback<FROM> {
  private Callback<TO> callback;
  private Converter<FROM, TO> converter;

  public CallbackAdapter(Callback<TO> target, Converter<FROM, TO> converter) {
    this.converter = converter;
    this.callback = target;
  }

  @Override
  public void completed(FROM result) {
    callback.completed(converter.convert(result));
  }

  @Override
  public void failed(Throwable throwable) {
    callback.failed(throwable);
  }
}
