package com.secucard.connect.channel;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.secucard.connect.model.annotation.ProductInfo;
import com.secucard.connect.model.general.Event;
import com.secucard.connect.util.jackson.DynamicTypeReference;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Utility class for mapping JSON to objects and back.
 * Uses a com.fasterxml.jackson.ObjectMapper instance internally,
 * it's thread safe and using singleton pattern is recommended.
 */
public class JsonMapper {
  protected ObjectMapper objectMapper = new ObjectMapper();

  protected static final TypeMap TYPE_REGISTRY = new TypeMap();

  private static final JsonMapper instance = new JsonMapper();

  private JsonMapper() {
  }

  public static JsonMapper get() {
    return instance;
  }

  @SuppressWarnings({"unchecked"})
  public <T> T map(String json, TypeReference typeReference) throws IOException {
    if (json == null) {
      return null;
    }

    T result;
    Type type = typeReference.getType();
    if (String.class.equals(type)) {
      result = (T) json.trim();
    } else {
      result = objectMapper.readValue(json, typeReference);
    }

    return result;
  }

  @SuppressWarnings({"unchecked"})
  public <T> T map(String json, Class<T> type) throws IOException {
    if (json == null) {
      return null;
    }

    T result;
    if (String.class.equals(type)) {
      result = (T) json.trim();
    } else {
      result = objectMapper.readValue(json, type);
    }

    return result;
  }

  /**
   * Serializes an object into JSON.
   */
  public String map(Object object) throws IOException {
    return objectMapper.writeValueAsString(object);
  }

  /**
   * Tries to map a JSON string into {@link com.secucard.connect.model.SecuObject}.
   * If not possible an instance of Map is returned or the JSON string itself.
   */
  public Object map(String json) throws IOException {
    Map map = map(json, Map.class);
    if (map == null) {
      return json;
    }

    Class type = null;
    TypeReference typeReference = null;

    Object typeId = map.get("type");
    if (typeId != null) {
      type = TYPE_REGISTRY.getType((String) typeId);
      if (type != null) {
        typeReference = new DynamicTypeReference(type);
      }
    }

    Object objectId = map.get("object");
    if (type != null && objectId != null && StringUtils.equalsIgnoreCase("general.events", (String) objectId)) {
     typeReference = new DynamicTypeReference(Event.class, type);
    }

    if (type != null) {
      return map(json, typeReference);
    }

    return map;
  }

  /**
   * Collects all classes annotated with {@link com.secucard.connect.model.annotation.ProductInfo}
   * for JSON deserialization purposes.
   */
  protected static class TypeMap extends HashMap<String, Class<?>> {
    {
      Reflections reflections = new Reflections("com.secucard.connect.model");
      Set<Class<?>> types = reflections.getTypesAnnotatedWith(ProductInfo.class);
      for (Class<?> type : types) {
        ProductInfo annotation = type.getAnnotation(ProductInfo.class);
        if (annotation != null) {
          put(annotation.resourceId().toLowerCase(), type);
        }
      }
    }

    public Class getType(String typeString) {
      return get(typeString.toLowerCase());
    }
  }
}
