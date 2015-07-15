package com.secucard.connect.net.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.secucard.connect.client.ProductService;
import com.secucard.connect.net.JsonMappingException;
import com.secucard.connect.net.util.jackson.DynamicTypeReference;
import com.secucard.connect.product.common.model.SecuObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility singleton class for mapping JSON to objects and back.
 * Uses a com.fasterxml.jackson.ObjectMapper instance internally,
 * it's thread safe and using singleton pattern is recommended.
 */
public class JsonMapper {
  protected ObjectReader objectReader;
  protected ObjectWriter objectWriter;
  private Map<String, Class<? extends SecuObject>> object2type;

  public void init(Collection<? extends ProductService<? extends SecuObject>> productServices) {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    object2type = new HashMap<>();
    for (ProductService<? extends SecuObject> service : productServices) {
      ProductService.ServiceMetaData<? extends SecuObject> md = service.getMetaData();
      object2type.put(md.product + "." + md.resource, md.resourceType);
    }
    objectReader = objectMapper.reader().withAttribute("secucardobjectmap", object2type);
    objectWriter = objectMapper.writer();
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
        result = objectReader.forType(typeReference).readValue(json);
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
    return objectReader.forType(type).readValue(json);
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
      result = objectReader.forType(type).readValue(json);
    }

    return result;
  }

  /**
   * Serializes an object into JSON.
   */
  public String map(Object object) throws IOException {
    return objectWriter.writeValueAsString(object);
  }

  /**
   * Tries to map a JSON string into {@link com.secucard.connect.product.common.model.SecuObject}.
   * If not possible an instance of Map is returned.
   */
  public Object map(String json) throws IOException {
    Map map = map(json, Map.class);
    if (map == null) {
      return json;
    }

    Object objectId = map.get(SecuObject.OBJECT_PROPERTY);

    if (objectId != null) {
      Class type = object2type.get((String) objectId);
      if (type != null) {
        return map(json, new DynamicTypeReference(type));
      }
    }

    return map;
  }
}
