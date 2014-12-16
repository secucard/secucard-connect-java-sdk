package com.secucard.connect.storage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryDataStorage extends DataStorage {
  private Map<String, Object> store = new ConcurrentHashMap<>();

  @Override
  public void save(String id, Object object, boolean replace) {
    if (!replace && store.containsKey(id)) {
      return;
    }
    store.put(id, object);
  }

  @Override
  public Object get(String id) {
    return store.get(id);
  }

  @Override
  public void clear(String id) {

  }
}
