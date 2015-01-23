package com.secucard.connect.channel;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.secucard.connect.model.SecuObject;
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

public class JsonMapper {
  protected ObjectMapper objectMapper = new ObjectMapper();
  protected static final TypeMap TYPE_REGISTRY = new TypeMap();

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
    Object object = map.get("object");
    Object type = map.get("type");
    if (object != null && type != null && StringUtils.equalsIgnoreCase("general.events", (String) object)) {
      Class eventType = TYPE_REGISTRY.getType((String) type);
      if (eventType != null) {
        return map(json, new DynamicTypeReference(Event.class, eventType));
      }
    }
    return map;
  }

  /**
   * Collects all subclasses of {@link SecuObject} from the package for JSON deserialization purposes.
   */
  protected static class TypeMap extends HashMap<String, Class<? extends SecuObject>> {
    {
      String name = SecuObject.class.getPackage().getName();
      Reflections reflections = new Reflections(name);
      Set<Class<? extends SecuObject>> subTypes = reflections.getSubTypesOf(SecuObject.class);
      for (Class<? extends SecuObject> type : subTypes) {
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
