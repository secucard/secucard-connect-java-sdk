package com.secucard.connect.channel;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.util.jackson.DynamicTypeReference;
import com.secucard.connect.util.jackson.ObjectIdTypeResolver;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Map;

/**
 * Utility class for mapping JSON to objects and back.
 * Uses a com.fasterxml.jackson.ObjectMapper instance internally,
 * it's thread safe and using singleton pattern is recommended.
 */
public class JsonMapper {
  protected ObjectMapper objectMapper = new ObjectMapper();

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
      try {
        result = objectMapper.readValue(json, typeReference);
      } catch (IOException e) {
        throw new JsonMappingException(json, e);
      }
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
   * If not possible an instance of Map is returned.
   */
  public Object map(String json) throws IOException {
    Map map = map(json, Map.class);
    if (map == null) {
      return json;
    }

    Object objectId = map.get(SecuObject.OBJECT_PROPERTY);

    if (objectId != null) {
      Class type = ObjectIdTypeResolver.getType((String) objectId);
      if (type != null) {
        return map(json, new DynamicTypeReference(type));
      }
    }

    return map;
  }
}
