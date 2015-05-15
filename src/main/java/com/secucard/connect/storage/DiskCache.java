package com.secucard.connect.storage;

import java.io.*;

/**
 * Writes objects and streams to disk.
 */
public class DiskCache extends DataStorage {
  private static final String OBJECTSTORE = "objectstore";
  private File cacheDir;
  private MemoryDataStorage store;
  private boolean bundleObjects = true; // bundle object to save in separate store and save store to disk

  public DiskCache(String cacheDir) throws IOException {
    init(cacheDir);
  }

  protected void init(String path) {
    cacheDir = new File(path);
    createCacheDirs();
  }

  private void createCacheDirs() {
    cacheDir.mkdirs();
    if (!cacheDir.exists()) {
      throw new DataStorageException("Can't create directory " + cacheDir);
    }
  }

  @Override
  public synchronized void save(String id, Object object, boolean replace) throws DataStorageException {
    if (!(object instanceof Serializable)) {
      throw new DataStorageException("Object to store must implement serializable");
    }

    if (!cacheDir.exists()) {
      createCacheDirs();
    }

    ObjectOutputStream out = null;
    try {
      File path;

      if (bundleObjects) {
        // save given object in separate store and save this whole store instead of the object itself as file
        // this approach should increase access performance, as well this way the store must not read every time from disk
        readStore();
        if (!store.saveInternal(id, object, replace)) {
          return;
        }
        id = OBJECTSTORE;
        object = store;
      }

      path = new File(cacheDir, id);

      if (!bundleObjects && path.exists() && !replace) {
        return;
      }

      out = new ObjectOutputStream(new FileOutputStream(path));
      out.writeObject(object);
      out.flush();
    } catch (IOException | ClassNotFoundException e) {
      throw new DataStorageException("Error writing object \"" + id + "\" to disk.", e);
    } finally {
      if (out != null) {
        try {
          out.close();
        } catch (IOException e) {
          // ignore
        }
      }
    }
  }

  @Override
  public synchronized void save(String id, InputStream in, boolean replace) throws DataStorageException {
    if (!cacheDir.exists()) {
      createCacheDirs();
    }

    // always save streams as separate files
    File path = new File(cacheDir, id);
    if (path.exists() && !replace) {
      return;
    }
    BufferedOutputStream out = null;
    BufferedInputStream bufferedIn = new BufferedInputStream(in);
    try {
      out = new BufferedOutputStream(new FileOutputStream(path));
      int b;
      while ((b = bufferedIn.read()) != -1) {
        out.write(b);
      }
      out.flush();
    } catch (IOException e) {
      throw new DataStorageException("Error writing stream \"" + id + "\" to disk.", e);
    } finally {
      try {
        bufferedIn.close();
        if (out != null) {
          out.close();
        }
      } catch (IOException e) {
        // ignore
      }
    }
  }

  @Override
  public synchronized Object get(String id) {
    try {
      if (bundleObjects) {
        readStore();
        return store.get(id);
      }
      InputStream stream = getStream(id);
      if (stream != null) {
        return new ObjectInputStream(stream).readObject();
      }
    } catch (IOException | ClassNotFoundException e) {
      throw new DataStorageException("Error reading object \"" + id + "\" from disk.", e);
    }
    return null;
  }

  @Override
  public synchronized InputStream getStream(String id) {
    File path = new File(cacheDir, id);
    try {
      if (path.exists() && path.length() > 0) {
        return new FileInputStream(path);
      }
    } catch (IOException e) {
      throw new DataStorageException("Error reading stream \"" + id + "\" from disk.", e);
    }
    return null;
  }

  @Override
  public synchronized void clear(final String pattern, final Long timestampMs) {
    if (!cacheDir.exists()) {
      return;
    }

    if (bundleObjects) {
      // also look in object store
      ObjectOutputStream out = null;
      try {
        readStore();
        store.clear(pattern, timestampMs);
        out = new ObjectOutputStream(new FileOutputStream(new File(cacheDir, OBJECTSTORE)));
        out.writeObject(store);
      } catch (IOException | ClassNotFoundException e) {
        throw new DataStorageException("Error deleting  file from disk.", e);
      } finally {
        try {
          if (out != null) {
            out.close();
          }
        } catch (IOException e) {
          // ignore
        }
      }
    }

    // delete cache files but not object store

    File[] paths = cacheDir.listFiles(new FileFilter() {
      @Override
      public boolean accept(File pathname) {
        String fileName = pathname.getName();
        return !OBJECTSTORE.equals(fileName) && wildCardMatch(fileName, pattern)
            && (timestampMs == null || pathname.lastModified() < timestampMs);
      }
    });

    for (File path : paths) {
      if (!path.delete()) {
        throw new DataStorageException("Error deleting  file \"" + path.toString() + "\" from disk.");
      }
    }
  }

  public synchronized int size() throws Exception {
    if (!cacheDir.exists()) {
      return 0;
    }

    String[] files = cacheDir.list();
    if (files == null) {
      return 0;
    }

    int size;
    if (new File(cacheDir, OBJECTSTORE).exists()) {
      size = files.length - 1;
      readStore();
      size += store.size();
    } else {
      size = files.length;
    }

    return size;
  }

  public synchronized void destroy() {
    if (!cacheDir.exists()) {
      return;
    }

    if (bundleObjects) {
      store = null;
    }

    File[] files = cacheDir.listFiles();
    for (File file : files) {
      if (!file.delete()) {
        throw new DataStorageException("Error deleting  file \"" + file.getPath() + "\" from disk.");
      }
    }

    deleteDir(cacheDir);
  }

  private void deleteDir(File file) {
    if (file != null && file.isDirectory() && file.list().length == 0) {

      if (!file.delete()) {
        throw new DataStorageException("Error deleting  file \"" + file.getPath() + "\" from disk.");
      }

      deleteDir(file.getParentFile());
    }
  }

  private void readStore() throws IOException, ClassNotFoundException {
    if (store == null) {
      // read from disk only if not exist yet
      File path = new File(cacheDir, OBJECTSTORE);
      if (path.exists() && path.length() > 0) {
        store = (MemoryDataStorage) new ObjectInputStream(new FileInputStream(path)).readObject();
      } else {
        store = new MemoryDataStorage();
      }
    }
  }
}
