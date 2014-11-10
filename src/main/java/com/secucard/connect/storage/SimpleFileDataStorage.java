package com.secucard.connect.storage;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Saves to file using Java object serialization. Store is a map.
 * Not very efficient - each access reads (and writes) the whole file.
 * Objects to save must implement serializable!
 */
public class SimpleFileDataStorage extends DataStorage {
  private File file;

  public SimpleFileDataStorage(String path) throws IOException {
    file = new File(path);
    file.createNewFile(); // just validation
  }

  @Override
  public void save(String id, Object object, boolean replace) throws DataStorageException {
    execute(object, id, replace, false);
  }

  @Override
  public <T> T get(String id) throws DataStorageException {
    return execute(null, id, false, false);
  }


  @Override
  public void clear(String id) {
    execute(null, id, false, true);
  }

  private synchronized <T> T execute(T object, String id, boolean replace, boolean clear) {
    try {

      Map<String, T> map;
      if (file.length() == 0) {
        map = new HashMap<>();
      } else {
        map = (Map<String, T>) new ObjectInputStream(new FileInputStream(file)).readObject();
      }

      if (object == null && !clear) {
        // get mode
        return (T) map.get(id);
      }

      if (clear) {
        Iterator<Map.Entry<String, T>> it = map.entrySet().iterator();
        boolean wildcard = id.contains("*");
        if (wildcard) {
          id = id.replace("*", "");
        }
        while (it.hasNext()) {
          Map.Entry<String, T> next = it.next();
          if (wildcard && next.getKey().contains(id) || next.getKey().equals(id)) {
            it.remove();
          }
        }
      }

      // save mode else
      if (!replace && map.containsKey(id)) {
        return null;
      }

      map.put(id, object);

      new ObjectOutputStream(new FileOutputStream(file)).writeObject(map);

      return null;
    } catch (Exception e) {
      throw new DataStorageException((e));
    }
  }
}
