package com.secucard.connect.util.jackson;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.annotation.ProductInfo;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Resolves object id string into SecuObject types or into collection type of SecuObject.
 * Falls back to Map if the id could not resolved.
 * todo: handle missing target properties, at the moment an exception will be thrown
 */
public class ObjectIdTypeResolver extends TypeIdResolverBase {
  public static final TypeMap TYPE_REGISTRY = new TypeMap();

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
    Class<?> type = getType(id);

    JavaType javatype = null;
    if (type != null) {
      javatype = SimpleType.construct(type);
    } else {
      javatype = MapType.construct(HashMap.class, SimpleType.construct(String.class), SimpleType.construct(Object.class));
    }


    if (JsonToken.END_ARRAY.equals(((DeserializationContext) context).getParser().getCurrentToken())) {
      // it is expected to get called here when reading the last token.
      javatype = CollectionType.construct(ArrayList.class, javatype);
    }

    return javatype;
  }


  public static Class getType(String objectId) {
    return TYPE_REGISTRY.getType(objectId);
  }

  /**
   * Collects all classes annotated with {@link com.secucard.connect.model.annotation.ProductInfo}
   * for JSON deserialization purposes.
   */
  protected static class TypeMap extends HashMap<String, Class<?>> {
    {
      // todo: maybe better to change to inspecting byte code instead, because this instantiates each class
      Reflections reflections = new Reflections("com.secucard.connect.model", new SubTypesScanner(false));
      Set<Class<?>> types = reflections.getSubTypesOf(Object.class);
      for (Class<?> type : types) {
        String resourceId;
        ProductInfo annotation = type.getAnnotation(ProductInfo.class);
        if (annotation != null) {
          resourceId = annotation.resourceId();
        } else {
          try {
            resourceId = (String) type.getField(SecuObject.OBJECT_FIELD).get(null);
          } catch (Exception e) {
            resourceId = null;
          }
        }

        if (resourceId != null) {
          put(resourceId.toLowerCase(), type);
        }
      }
    }

    public Class getType(String typeString) {
      if (typeString == null) {
        return null;
      }
      return get(typeString.toLowerCase());
    }
  }
}
