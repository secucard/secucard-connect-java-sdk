package com.secucard.connect.client;

import java.io.InputStream;

/**
 * Gives basic access to the data store implementation.
 * Used to persist any data, like access tokens or downloaded media.
 */
public abstract class DataStorage {

  /**
   * Put an object in th store using the given id.
   * Must handle also InputStream instances provided as input.
   *
   * @param id      An unique id of the object.
   * @param object  The object to save.
   * @param replace True if a existing object of same id should be overwritten, false else.
   * @throws DataStorageException If an error happens.
   */
  public abstract void save(String id, Object object, boolean replace) throws DataStorageException;

  /**
   * Put a stream object in th store using the given id.
   * Must handle also InputStream instances provided as input.
   *
   * @param id      An unique id of the object.
   * @param in      The object to save.
   * @param replace True if a existing object of same id should be overwritten, false else.
   * @throws DataStorageException If an error happens.
   */
  public abstract void save(String id, InputStream in, boolean replace) throws DataStorageException;


  /**
   * Like {@link #save(String, Object, boolean)} but replaces every time.
   */
  public void save(String id, Object object) throws DataStorageException {
    save(id, object, true);
  }

  /**
   * Like {@link #save(String, java.io.InputStream, boolean)} but replaces every time.
   */
  public void save(String id, InputStream in) throws DataStorageException {
    save(id, in, true);
  }

  /**
   * Returns the stored object for the provided id.
   * May return InputStream instances, in this case the caller is responsible for closing
   *
   * @param id Id of the object to get.
   * @return The stored content or null if nothing found.
   * @throws DataStorageException If an error happens.
   */
  public abstract Object get(String id) throws DataStorageException;


  /**
   * Return an input stream object for the given id.
   * The caller is responsible for closing the stream properly when done.
   *
   * @param id The id of the stream to get.
   * @return The stream or null if nothing found.
   * @throws DataStorageException
   */
  public abstract InputStream getStream(String id) throws DataStorageException;

  /**
   * Remove all data for a given id which are older as a given timestamp.
   * The id may contain a wildcard sign "*" - in this case all matching entries are removed.
   *
   * @param id          The id.
   * @param timestampMs A Unix timestamp in ms, null to omit.
   * @throws DataStorageException If an error happens.
   */
  public abstract void clear(String id, Long timestampMs) throws DataStorageException;

  /**
   * Removing all data.
   *
   * @throws DataStorageException If an error happens.
   */
  public void clear() throws DataStorageException {
    clear("*", null);
  }

  /**
   * Removing all data older as the given timestamp.
   *
   * @param timestampMs A Unix timestamp in ms.
   * @throws DataStorageException If an error happens.
   */
  public void clear(long timestampMs) throws DataStorageException {
    clear("*", timestampMs);
  }

  /**
   * Clear the storage and remove any artifacts created by the store like directories.
   *
   * @throws DataStorageException If an error happens.
   */
  public abstract void destroy() throws DataStorageException;

  protected static boolean wildCardMatch(String text, String pattern) {
    String[] cards = pattern.split("\\*");
    for (String card : cards) {
      int idx = text.indexOf(card);
      if (idx == -1) {
        return false;
      }
      text = text.substring(idx + card.length());
    }
    return true;
  }

  public static class DataStorageException extends RuntimeException {
    public DataStorageException(String message) {
      super(message);
    }

    public DataStorageException(String message, Throwable cause) {
      super(message, cause);
    }

    public DataStorageException(Throwable cause) {
      super(cause);
    }
  }
}
