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
  private Map<String, Object> store;

  public SimpleFileDataStorage(String path) throws IOException {
    file = new File(path);
    file.createNewFile(); // just validation
  }

  @Override
  public synchronized void save(String id, Object object, boolean replace) throws DataStorageException {
    try {
      readStore();
      if (store == null) {
        store = new HashMap<>();
      }
      if (!replace && store.containsKey(id)) {
        return;
      }
      store.put(id, object);
      writeStore();
    } catch (IOException | ClassNotFoundException e) {
      throw new DataStorageException(e);
    }
  }

  @Override
  public synchronized Object get(String id) throws DataStorageException {

    try {
      readStore();
    } catch (IOException | ClassNotFoundException e) {
      throw new DataStorageException(e);
    }

    if (store == null) {
      return null;
    }

    return store.get(id);
  }

  @Override
  public synchronized void clear(String id) {

    try {
      readStore();

      if (store == null) {
        return;
      }

      if (id == null || "*".equals(id)) {
        if (!file.delete()) {
          throw new DataStorageException("Cannot delete store");
        }
        return;
      }

      if (id.contains("*")) {
        Iterator<String> it = store.keySet().iterator();

        while (it.hasNext()) {
          String key = it.next();
          if (wildCardMatch(key, id)) {
            it.remove();
          }
        }
        writeStore();
      } else {
        store.remove(id);
        writeStore();
      }

    } catch (IOException | ClassNotFoundException e) {
      throw new DataStorageException(e);
    }
  }

  private void writeStore() throws IOException {
    new ObjectOutputStream(new FileOutputStream(file)).writeObject(store);
  }


  private void readStore() throws IOException, ClassNotFoundException {
    if (!file.exists() || file.length() == 0) {
      store = null;
      return;
    }

    store = (Map<String, Object>) new ObjectInputStream(new FileInputStream(file)).readObject();
  }
}
