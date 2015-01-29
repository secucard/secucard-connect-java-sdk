package com.secucard.connect.event;

public interface EventListener<T> extends java.util.EventListener {
  public void onEvent(T event);
}
