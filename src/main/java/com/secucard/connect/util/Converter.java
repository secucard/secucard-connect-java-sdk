package com.secucard.connect.util;

public interface Converter<FROM, TO> {
  public TO convert(FROM value);
}
