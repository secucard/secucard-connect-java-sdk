package com.secucard.connect.channel;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.annotation.ProductInfo;
import com.secucard.connect.model.general.Event;
import com.secucard.connect.util.jackson.DynamicTypeReference;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
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
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
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

  public <T> T map(URL json, Class<T> type) throws IOException {
    if (json == null) {
      return null;
    }
    return objectMapper.readValue(json, type);
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

    Object objectId = map.get("object");

    if (objectId != null) {
      type = TYPE_REGISTRY.getType((String) objectId);
      if (type != null) {
        typeReference = new DynamicTypeReference(type);
      }
    }

    if (objectId != null && StringUtils.equalsIgnoreCase("general.events", (String) objectId)) {
      type = TYPE_REGISTRY.getType((String) objectId);
      if (type != null) {
        typeReference = new DynamicTypeReference(Event.class, type);
      }
    }


    if (type != null) {
      return map(json, typeReference);
    }

    return map;
  }

  public Event mapEvent(String json) throws IOException {
    Map map = map(json, Map.class);
    if (map == null) {
      return null;
    }

    JsonNode tree = objectMapper.readTree(json);

    JsonNode object = tree.get(SecuObject.OBJECT_PROPERTY);
    if (object != null && object.textValue().startsWith(Event.OBJECT_PROPERTY_PREFIX)) {
      Event event = objectMapper.readValue(json, Event.class);
      Class dataType = TYPE_REGISTRY.getType(event.getTarget());
      if (dataType != null) {
        JsonNode data = tree.get(Event.DATA_PROPERTY);
        if (data != null) {
          event.setData(objectMapper.reader(new DynamicTypeReference(List.class, dataType)).readValue(data));
        }
      }
      return event;
    }

    return null;
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
            resourceId = (String) type.getField("OBJECT").get(null);
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
