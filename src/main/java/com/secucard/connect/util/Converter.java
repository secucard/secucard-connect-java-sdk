package com.secucard.connect.util;

import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.transport.Result;

import java.util.List;

/**
 * Utility class which can convert object instances by applying a conversion operation.
 * Provides some standard converters.
 *
 * @param <FROM>
 * @param <TO>
 */
public abstract class Converter<FROM, TO> {
  public abstract TO convert(FROM value);

  public static class ToListConverter<T> extends Converter<ObjectList<T>, List<T>> {
    @Override
    public List<T> convert(ObjectList<T> value) {
      if (value == null || value.getList() == null || value.getList().size() == 0) {
        return null;
      }
      return value.getList();
    }
  }

  public static class ToBooleanConverter extends Converter<Result, Boolean> {
    @Override
    public Boolean convert(Result value) {
      return value == null ? Boolean.FALSE : Boolean.parseBoolean(value.getResult());
    }
  }
}
