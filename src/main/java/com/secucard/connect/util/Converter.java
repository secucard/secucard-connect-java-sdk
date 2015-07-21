/*
 * Copyright (c) 2015. hp.weber GmbH & Co secucard KG (www.secucard.com)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.secucard.connect.util;


import com.secucard.connect.product.common.model.ObjectList;
import com.secucard.connect.product.common.model.Result;

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

  private static class ToBooleanConverter extends Converter<Result, Boolean> {
    @Override
    public Boolean convert(Result value) {
      return value == null ? Boolean.FALSE : Boolean.parseBoolean(value.getResult());
    }
  }

  public static final Converter<Result, Boolean> RESULT2BOOL = new ToBooleanConverter();
}
