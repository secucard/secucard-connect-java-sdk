package com.secucard.connect.storage;

import java.io.*;

/**
 * Writes objects and streams to disk.
 */
public class DiskCache extends DataStorage implements Serializable {
  private transient File cacheDir;
  private transient ObjectStore store;
  private transient boolean bundleObjects = true; // bundle object to save in separate store and save store to disk

  public DiskCache(String cacheDir) throws IOException {
    init(cacheDir);
  }

  protected void init(String path) {
    cacheDir = new File(path);
    cacheDir.mkdir();
  }

  @Override
  public synchronized void save(String id, Object object, boolean replace) throws DataStorageException {
    if (!(object instanceof Serializable)) {
      throw new DataStorageException("Object to store must implement serializable");
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
        id = "objectstore";
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
  public Object get(String id) {
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
  public InputStream getStream(String id) {
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
        out = new ObjectOutputStream(new FileOutputStream(new File(cacheDir, "objectstore")));
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

    FileFilter filter = new FileFilter() {
      @Override
      public boolean accept(File pathname) {
        return wildCardMatch(pathname.getPath(), pattern)
            && (timestampMs == null || pathname.lastModified() < timestampMs);
      }
    };

    File[] paths = cacheDir.listFiles(filter);

    for (File path : paths) {
      if (!path.delete()) {
        throw new DataStorageException("Error deleting  file \"" + path.toString() + "\" from disk.");
      }
    }
  }

  public int size() {
    if (!cacheDir.exists()) {
      return 0;
    }
    String[] files = cacheDir.list();
    return files == null ? 0 : files.length;
  }

  public void destroy() {
    if (!cacheDir.exists()) {
      return;
    }

    if (bundleObjects) {
      store = null;
    }

    String[] files = cacheDir.list();
    for (String file : files) {
      new File(file).delete();
    }

    cacheDir.delete();
  }

  private void readStore() throws IOException, ClassNotFoundException {
    if (store == null) {
      // read from disk only if not exist yet
      File path = new File(cacheDir, "objectstore");
      if (path.exists() && path.length() > 0) {
        store = (ObjectStore) new ObjectInputStream(new FileInputStream(path)).readObject();
      } else {
        store = new ObjectStore();
      }
    }
  }

  private class ObjectStore extends MemoryDataStorage {

    @Override
    public boolean saveInternal(String id, Object object, boolean replace) {
      return super.saveInternal(id, object, replace);
    }
  }
}
