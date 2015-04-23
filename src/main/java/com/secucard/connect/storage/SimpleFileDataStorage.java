package com.secucard.connect.storage;

import java.io.*;

/**
 * File based store.
 * Utilizes Java object serialization for persistence, so objects to save must implement serializable.
 * Just a basic solution, maybe not the most efficient (actually a MemoryDataStorage instance is maintaned internally
 * and serialized to a single file after each write to keep the state).
 */
public class SimpleFileDataStorage extends DataStorage {
  private File file;
  private MemoryDataStorage store;

  public SimpleFileDataStorage(String cacheDir) throws IOException {
    new File(cacheDir).mkdir();
    file = new File(cacheDir + File.separator + "scf");
    file.createNewFile();
  }

  public int size() throws Exception {
    return readStore().size();
  }

  public boolean remove() {
    clear();
    boolean deleted = file.delete();
    file.getParentFile().delete(); // delete also dir if empty
    return deleted;
  }

  @Override
  public Object get(String id) {
    return getInternal(id);
  }

  @Override
  public InputStream getStream(String id) {
    return (InputStream) getInternal(id);
  }

  @Override
  public void save(String id, Object object, boolean replace) throws DataStorageException {
    saveInternal(id, object, replace);
  }

  @Override
  public void save(String id, InputStream in, boolean replace) throws DataStorageException {
    saveInternal(id, in, replace);
  }

  @Override
  public synchronized void clear(String id, Long timestampMs) {
    try {
      readStore().clear(id, timestampMs);
      writeStore();
    } catch (Exception e) {
      throw new DataStorageException(e);
    }
  }

  @Override
  public void destroy() {
    remove();
  }

  private synchronized Object getInternal(String id) {
    try {
      return readStore().get(id);
    } catch (Exception e) {
      throw new DataStorageException(e);
    }
  }

  private synchronized void saveInternal(String id, Object object, boolean replace) {
    try {
      readStore().save(id, object, replace);
      writeStore();
    } catch (Exception e) {
      throw new DataStorageException(e);
    }
  }

  private MemoryDataStorage readStore() throws Exception {
    if (store == null) {
      if (file.exists() && file.length() > 0) {
        store = (MemoryDataStorage) new ObjectInputStream(new FileInputStream(file)).readObject();
      } else {
        store = new MemoryDataStorage();
      }
    }
    return store;
  }

  private void writeStore() throws IOException {
    new ObjectOutputStream(new FileOutputStream(file)).writeObject(store);
  }
}
