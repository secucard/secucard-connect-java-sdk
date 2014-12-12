package com.secucard.connect.channel.stomp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.transport.Message;
import com.secucard.connect.util.jackson.DynamicTypeReference;

import java.io.IOException;

public class JsonBodyMapper implements BodyMapper {

  private ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public <T> Message<ObjectList<T>> toMessageList(Class<T> type, String body) throws IOException {
    return objectMapper.readValue(body, new DynamicTypeReference(Message.class,
        new DynamicTypeReference.TypeInfo(ObjectList.class, type)));
  }

  public <T> Message<T> toMessage(Class<T> type, String body) throws IOException {
    return objectMapper.readValue(body, new DynamicTypeReference(Message.class, type));
  }

  @Override
  public String map(Message body) throws IOException {
    return objectMapper.writeValueAsString(body);
  }
}
