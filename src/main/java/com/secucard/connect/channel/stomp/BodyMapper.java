package com.secucard.connect.channel.stomp;

import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.transport.Message;

import java.io.IOException;

/**
 * Interface for mapping Stomp string message body to java objects.
 */
interface BodyMapper {
  <T> Message<T> toMessage(Class<T> type, String body) throws IOException;

  <T> Message<ObjectList<T>> toMessageList(Class<T> type, String body) throws IOException;

  String map(Message body) throws IOException;
}
