package com.secucard.connect.storage;

import java.io.*;

/**
 * Saves to file using Java object serialization of the memory based storage.
 * Not very efficient - each access reads (and writes) the whole file.
 * Objects to save must implement serializable!
 */
public class SimpleFileDataStorage extends DataStorage {
  private File file;
  private MemoryDataStorage store;

  public SimpleFileDataStorage(String path) throws IOException {
    file = new File(path);
    file.createNewFile(); // just validation
  }

  public Integer size() throws IOException, ClassNotFoundException {
    readStore();
    if (store == null) {
      return null;
    }
    return store.size();
  }

  public boolean remove() {
    return file.delete();
  }

  @Override
  public synchronized void save(String id, Object object, boolean replace) throws DataStorageException {
    saveInternal(id, object, replace);
  }

  private void saveInternal(String id, Object object, boolean replace) {
    try {
      readStore();
      if (store == null) {
        store = new MemoryDataStorage();
      }
      store.save(id, object, replace);
      writeStore();
    } catch (IOException | ClassNotFoundException e) {
      throw new DataStorageException(e);
    }
  }

  @Override
  public void save(String id, InputStream in, boolean replace) throws DataStorageException {
    saveInternal(id, in, replace);
  }

  @Override
  public synchronized Object get(String id) throws DataStorageException {
    return getInternal(id);
  }

  private Object getInternal(String id) {
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
  public InputStream getStream(String id) {
    return (InputStream) getInternal(id);
  }

  @Override
  public synchronized void clear(String id, Long timestampMs) {
    try {
      readStore();

      if (store == null) {
        return;
      }

      store.clear(id, timestampMs);
      writeStore();

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

    store = (MemoryDataStorage) new ObjectInputStream(new FileInputStream(file)).readObject();
  }
}
