/*
 * Copyright (c) 2015. hp.weber GmbH & Co secucard KG (www.secucard.com)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.secucard.connect.client;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;

/**
 * Writes objects and streams to disk.
 */
public class OfflineCache extends DataStorage {
  private File directory;

  public OfflineCache(String path) {
    try {
      init(path);
    } catch (Exception e) {
      throw new ClientError("Error initializing data storage.", e);
    }
  }

  protected void init(String path) {
    directory = new File(path);
    createDirectoryIfNeeded();
  }

  private void createDirectoryIfNeeded() {
    if (!directory.exists()) {
      directory.mkdirs();
      if (!directory.exists()) {
        throw new DataStorageException("Can't create directory " + directory);
      }
    }
  }

  @Override
  public synchronized void save(String id, Object object, boolean replace) throws DataStorageException {
    if (!(object instanceof Serializable)) {
      throw new DataStorageException("Object to store must implement serializable");
    }

    createDirectoryIfNeeded();

    ObjectOutputStream out = null;
    try {
      File file = new File(directory, id);

      if (file.exists() && !replace) {
        return;
      }

      out = new ObjectOutputStream(new FileOutputStream(file));
      out.writeObject(object);
      out.flush();
    } catch (IOException e) {
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
    throw new DataStorageException("This operation is not supported");
  }

  @Override
  public synchronized Object get(String id) {
    try {
      InputStream stream = getStream(id);
      if (stream != null) {
        try {
          return new ObjectInputStream(stream).readObject();
        } finally {
          try {
            stream.close();
          } catch (IOException e) {
            // ignore
          }
        }
      }
    } catch (IOException | ClassNotFoundException e) {
      throw new DataStorageException("Error reading object \"" + id + "\" from disk.", e);
    }
    return null;
  }

  @Override
  public synchronized InputStream getStream(String id) {
    File path = new File(directory, id);
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
    if (!directory.exists()) {
      return;
    }

    File[] paths = directory.listFiles(new FileFilter() {
      @Override
      public boolean accept(File pathname) {
        String fileName = pathname.getName();
        return wildCardMatch(fileName, pattern)
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
    if (!directory.exists()) {
      return 0;
    }

    String[] files = directory.list();
    if (files == null) {
      return 0;
    }

    return files.length;
  }

  public synchronized File[] getFiles() {
    File[] files = directory.listFiles(new FileFilter() {
      @Override
      public boolean accept(File pathname) {
        return pathname.isFile();
      }
    });
    if (files != null) {
      Arrays.sort(files);
    }
    return files;
  }

  public synchronized void destroy() {
    if (!directory.exists()) {
      return;
    }

    File[] files = directory.listFiles();
    for (File file : files) {
      if (!file.delete()) {
        throw new DataStorageException("Error deleting  file \"" + file.getPath() + "\" from disk.");
      }
    }

    deleteDir(directory);
  }

  private void deleteDir(File file) {
    if (file != null && file.isDirectory() && file.list().length == 0) {

      if (!file.delete()) {
        throw new DataStorageException("Error deleting  file \"" + file.getPath() + "\" from disk.");
      }

      deleteDir(file.getParentFile());
    }
  }

  @Override
  public String toString() {
    return "OfflineCache{" +
        "directory=" + directory +
        "} " + super.toString();
  }
}
