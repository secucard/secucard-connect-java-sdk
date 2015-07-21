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

package com.secucard.connect.net.util.jackson;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.secucard.connect.product.common.model.SecuObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Resolves object id string into SecuObject types or into collection type of SecuObject.
 * Falls back to Map if the id could not resolved.
 * todo: handle missing target properties, at the moment an exception will be thrown
 */
public class ObjectIdTypeResolver extends TypeIdResolverBase {

  @Override
  public String idFromValue(Object value) {
    return this.idFromValueAndType(value, null);
  }

  @Override
  public String idFromValueAndType(Object value, Class<?> suggestedType) {
    if (value != null && value instanceof SecuObject) {
      return ((SecuObject) value).getObject();
    }

    if (suggestedType != null && SecuObject.class.isAssignableFrom(suggestedType)) {
      try {
        Field fields = suggestedType.getDeclaredField(SecuObject.OBJECT_FIELD);
        return (String) fields.get(null);
      } catch (Exception e) {
        throw new IllegalStateException("Missing static OBJECT field: " + suggestedType);
      }
    }
    return null;
  }

  @Override
  public JsonTypeInfo.Id getMechanism() {
    return JsonTypeInfo.Id.NAME;
  }

  @Override
  public JavaType typeFromId(DatabindContext context, String id) {
    DeserializationContext ctx = (DeserializationContext) context;
    Map<String, Class> map = (Map) ctx.getAttribute("secucardobjectmap");

    Class type = map.get(id);

    JavaType javatype;
    if (type == null) {
      javatype = MapType.construct(HashMap.class, SimpleType.construct(String.class), SimpleType.construct(Object.class));
    } else {
      javatype = SimpleType.construct(type);
    }

    if (JsonToken.END_ARRAY.equals(ctx.getParser().getCurrentToken())) {
      // it is expected to get called here when reading the last token.
      javatype = CollectionType.construct(ArrayList.class, javatype);
    }

    return javatype;
  }
}
