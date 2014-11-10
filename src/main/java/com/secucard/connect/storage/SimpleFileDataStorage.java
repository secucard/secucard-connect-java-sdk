package com.secucard.connect.storage;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class SimpleFileDataStorage extends DataStorage {

  private File file;

  public SimpleFileDataStorage() {
  }

  public SimpleFileDataStorage(String path) throws IOException {
    file = new File(path);
    file.createNewFile();
  }


  @Override
  public void save(String id, Object object, boolean replace) throws DataStorageException {
    handle(object, id, replace);
  }

  @Override
  public <T> T get(String id) throws DataStorageException {
    return handle(null, id, false);
  }

  private synchronized <T> T handle(T object, String id, boolean replace) {
    try {
      Map<String, T> map;
      if (file.length() != 0) {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
        map = (Map<String, T>) in.readObject();
      } else {
        map = new HashMap<>();
      }
      if (object == null) {
        return (T) map.get(id);
      }
      if (!replace && map.containsKey(id)) {
        return null;
      }
      map.put(id, object);
      ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(file));
      o.writeObject(map);
      return null;
    } catch (Exception e) {
      throw new DataStorageException((e));
    }
  }
}
