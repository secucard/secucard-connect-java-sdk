package com.secucard.connect.storage;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Writes objects and streams to disk.
 */
public class DiskCache extends DataStorage {
  private String cacheDir;
  private ObjectStore store;
  boolean bundleObjects = true; // bundle object to save in separate store and save store to disk

  public DiskCache(String cacheDir) throws IOException {
    init(cacheDir);
  }

  protected void init(String cacheDir) throws IOException {
    Path path = Paths.get(cacheDir);
    Files.createDirectories(path);
    this.cacheDir = cacheDir;
  }

  @Override
  public synchronized void save(String id, Object object, boolean replace) throws DataStorageException {
    if (!(object instanceof Serializable)) {
      throw new DataStorageException("Object to store must implement serializable");
    }
    ObjectOutputStream out = null;
    try {
      Path path;

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

      path = Paths.get(cacheDir, id);

      if (!bundleObjects && Files.exists(path) && !replace) {
        return;
      }

      out = new ObjectOutputStream(Files.newOutputStream(path));
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
    Path path = Paths.get(cacheDir, id);
    if (Files.exists(path) && !replace) {
      return;
    }
    BufferedOutputStream out = null;
    BufferedInputStream bufferedIn = new BufferedInputStream(in);
    try {
      out = new BufferedOutputStream(Files.newOutputStream(path));
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
    Path path = Paths.get(cacheDir, id);
    try {
      if (Files.exists(path) && Files.size(path) > 0) {
        return Files.newInputStream(path);
      }
    } catch (IOException e) {
      throw new DataStorageException("Error reading stream \"" + id + "\" from disk.", e);
    }
    return null;
  }

  @Override
  public synchronized void clear(final String pattern, final Long timestampMs) {
    Path cache = Paths.get(cacheDir);
    if (!Files.exists(cache)) {
      return;
    }

    if (bundleObjects) {
      // also look in object store
      ObjectOutputStream out = null;
      try {
        readStore();
        store.clear(pattern, timestampMs);
        out = new ObjectOutputStream(Files.newOutputStream(Paths.get(cacheDir, "objectstore")));
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

    DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
      @Override
      public boolean accept(Path entry) throws IOException {
        return wildCardMatch(entry.getFileName().toString(), pattern)
            && (timestampMs == null || Files.getLastModifiedTime(entry).toMillis() < timestampMs);
      }
    };

    DirectoryStream<Path> paths = null;
    try {
      paths = Files.newDirectoryStream(cache, filter);
    } catch (IOException e) {
      throw new DataStorageException("Error listing directory contents of \"" + cacheDir + "\"", e);
    }

    for (Path path : paths) {
      try {
        Files.delete(path);
      } catch (IOException e) {
        throw new DataStorageException("Error deleting  file \"" + path.toString() + "\" from disk.", e);
      }
    }
  }

  public int size() {
    String[] files = new File(cacheDir).list();
    return files == null ? 0 : files.length;
  }

  public void destroy() {
    Path cache = Paths.get(cacheDir);
    if (!Files.exists(cache)) {
      return;
    }

    if (bundleObjects) {
      store = null;
    }

    String[] files = new File(cacheDir).list();
    for (String file : files) {
      new File(file).delete();
    }

    new File(cacheDir).delete();
  }

  private void readStore() throws IOException, ClassNotFoundException {
    if (store == null) {
      // read from disk only if not exist yet
      Path path = Paths.get(cacheDir, "objectstore");
      if (Files.exists(path) && Files.size(path) > 0) {
        store = (ObjectStore) new ObjectInputStream(Files.newInputStream(path)).readObject();
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
