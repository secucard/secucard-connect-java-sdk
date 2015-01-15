package com.secucard.connect.storage;

import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryDataStorage extends DataStorage implements Serializable {
  private Map<String, Item> store = new ConcurrentHashMap<>();


  @Override
  public void save(String id, Object object, boolean replace) {
    saveInternal(id, object, replace);
  }

  @Override
  public void save(String id, InputStream in, boolean replace) throws DataStorageException {
    saveInternal(id, in, replace);
  }

  public int size() {
    return store.size();
  }

  private void saveInternal(String id, Object object, boolean replace) {
    if (!replace && store.containsKey(id)) {
      return;
    }
    Item item = new Item();
    try {
      if (object instanceof InputStream) {
        BufferedInputStream in = new BufferedInputStream((InputStream) object);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int b;
        while ((b = in.read()) != -1) {
          bos.write(b);
        }
        in.close();
        item.type = "is";
        item.value = bos.toByteArray();
      } else {
        item.type = null;
        item.value = object;
      }
    } catch (IOException e) {
      throw new DataStorageException(e);
    }
    item.time = System.currentTimeMillis();
    store.put(id, item);
  }

  @Override
  public Object get(String id) {
    return getInternal(id);
  }

  private Object getInternal(String id) {
    Object o = null;
    Item item = store.get(id);
    if (item != null) {
      if ("is".equals(item.type)) {
        o = new ByteArrayInputStream((byte[]) item.value);
      } else {
        o = item.value;
      }
    }
    return o;
  }

  @Override
  public InputStream getStream(String id) {
    return (InputStream) getInternal(id);
  }

  @Override
  public void clear(String id, Long timestampMs) {
    if (id == null) {
       return;
    }

    if ("*".equals(id) && timestampMs == null) {
      store.clear();
      return;
    }

    Iterator<Map.Entry<String, Item>> it = store.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<String, Item> entry = it.next();
      if (wildCardMatch(entry.getKey(), id)) {
        Item item = entry.getValue();
        if (timestampMs == null || item.time == null || item.time < timestampMs) {
          it.remove();
        }
      }
    }
  }

  private class Item implements Serializable {
    public Long time;
    public String type;
    public Object value;
  }

}
