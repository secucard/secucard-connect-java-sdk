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

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.reflect.TypeUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Dynamic type reference utility for supporting mapping parametrized types like ObjectList<T> with jackson from JSON
 * without the need of using static type parameters.
 * Abusing of the original TypeReference class...
 */
public class DynamicTypeReference<T> extends TypeReference<Void> {
  private Type type;

  public DynamicTypeReference(Class type) {
    this.type = type;
  }

  public DynamicTypeReference(Class type, Type... types) {
    this.type = TypeUtils.parameterize(type, types);
  }

  public DynamicTypeReference(Class type, TypeInfo typeInfo) {
    this.type = TypeUtils.parameterize(type, from(typeInfo));
  }

  public DynamicTypeReference(TypeInfo typeInfo) {
    this.type = from(typeInfo);
  }

  @Override
  public Type getType() {
    return type;
  }

  private static ParameterizedType from(TypeInfo typeInfo) {
    Type[] typeArguments = new Type[0];
    if (typeInfo.typeInfo != null) {
      typeArguments = new Type[]{from(typeInfo.typeInfo)};
    } else if (typeInfo.typeArgs != null) {
      typeArguments = typeInfo.typeArgs;
    }
    return TypeUtils.parameterize(typeInfo.rawType, typeArguments);
  }

  public static class TypeInfo {
    public Class rawType;
    public Type[] typeArgs;
    public TypeInfo typeInfo;

    public TypeInfo(Class rawType, Type... typeArgs) {
      this.rawType = rawType;
      this.typeArgs = typeArgs;
      this.typeInfo = null;
    }

    public TypeInfo(Class rawType, TypeInfo typeInfo) {
      this.rawType = rawType;
      this.typeInfo = typeInfo;
      this.typeArgs = null;
    }
  }
}
