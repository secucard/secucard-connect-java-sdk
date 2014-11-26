package com.secucard.connect.channel.stomp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.general.merchant.Merchant;
import com.secucard.connect.model.general.skeleton.Skeleton;
import com.secucard.connect.model.smart.Device;
import com.secucard.connect.model.smart.Ident;
import com.secucard.connect.model.smart.Result;
import com.secucard.connect.model.smart.Transaction;
import com.secucard.connect.model.transport.InvocationResult;
import com.secucard.connect.model.transport.Message;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JsonBodyMapper implements BodyMapper {

  private ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public <T> Message<ObjectList<T>> toMessageList(Class type, String body) throws IOException {
    TypeReference[] refs = TYPE_2_TYPEREFS.get(type);
    if (refs == null) {
      throw new IllegalArgumentException("Unknown type " + type);
    }
    return objectMapper.readValue(body, refs[1]);
  }

  public <T> Message<T> toMessage(Class type, String body) throws IOException {
    TypeReference[] refs = TYPE_2_TYPEREFS.get(type);
    if (refs == null) {
      throw new IllegalArgumentException("Unknown type " + type);
    }
    return objectMapper.readValue(body, refs[0]);
  }

  @Override
  public String map(Message body) throws IOException {
    return objectMapper.writeValueAsString(body);
  }


  private final static Map<Class, TypeReference[]> TYPE_2_TYPEREFS = new HashMap<>();
  static {
    TYPE_2_TYPEREFS.put(Skeleton.class, new TypeReference[]{
        new TypeReference<Message<Skeleton>>() {
        }, new TypeReference<Message<ObjectList<Skeleton>>>() {
    }});

    TYPE_2_TYPEREFS.put(InvocationResult.class, new TypeReference[]{
        new TypeReference<Message<InvocationResult>>() {
        }, new TypeReference<Message<ObjectList<InvocationResult>>>() {
    }});

    TYPE_2_TYPEREFS.put(Merchant.class, new TypeReference[]{
        new TypeReference<Message<Merchant>>() {
        }, new TypeReference<Message<ObjectList<Merchant>>>() {
    }});

    TYPE_2_TYPEREFS.put(Ident.class, new TypeReference[]{
        new TypeReference<Message<Ident>>() {
        }, new TypeReference<Message<ObjectList<Ident>>>() {
    }});

    TYPE_2_TYPEREFS.put(Transaction.class, new TypeReference[]{
        new TypeReference<Message<Transaction>>() {
        }, new TypeReference<Message<ObjectList<Transaction>>>() {
    }});

    TYPE_2_TYPEREFS.put(Device.class, new TypeReference[]{
        new TypeReference<Message<Device>>() {
        }, new TypeReference<Message<ObjectList<Device>>>() {
    }});

    TYPE_2_TYPEREFS.put(Map.class, new TypeReference[]{
        new TypeReference<Message<Map>>() {
        }, new TypeReference<Message<ObjectList<Map>>>() {
    }});

    TYPE_2_TYPEREFS.put(Result.class, new TypeReference[]{
        new TypeReference<Message<Result>>() {
        }, new TypeReference<Message<ObjectList<Result>>>() {
    }});
  }
}
