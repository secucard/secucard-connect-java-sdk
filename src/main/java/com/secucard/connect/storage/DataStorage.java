package com.secucard.connect.storage;

import java.io.InputStream;

/**
 * Gives basic access to the data store implementation.
 * Used to persist any data, like acess tokens or downloaded media.
 */
public abstract class DataStorage {

  /**
   * Save a object for an id.
   * Must handle also InputStream instances provided as input.
   *
   * @param id      An unique id of the object.
   * @param object  The object to save.
   * @param replace True if a existing object of same id should be overwritten, false else.
   * @throws DataStorageException if a error ocurrs.
   */
  public abstract void save(String id, Object object, boolean replace) throws DataStorageException;

  public abstract void save(String id, InputStream in, boolean replace) throws DataStorageException;

  public void save(String id, Object object) throws DataStorageException {
    save(id, object, true);
  }

  public void save(String id, InputStream in) throws DataStorageException {
    save(id, in, true);
  }

  /**
   * Returns the stored object for the provided id.
   * May return InputStream instances, in this case the caller is responsible for closing
   *
   * @param id Id of the object to get.
   * @return The stored content or null if nothing available.
   */
  public abstract Object get(String id);

  public abstract InputStream getStream(String id);

  /**
   */
  /**
   * Remove all data for a given id which are older as a given timestamp.
   * The id may contain a wildcard sign "*" - in this case all matching entries are removed.
   *
   * @param id        The id.
   * @param timestampMs A Unix timestamp in ms, null to omit.
   */
  public abstract void clear(String id, Long timestampMs);

  /**
   * Removing all data.
   */
  public void clear() {
    clear("*", null);
  }

  /**
   * Removing all data older as the given timestamp.
   *
   * @param timestampMs A Unix timestamp in ms.
   */
  public void clear(long timestampMs) {
    clear("*", timestampMs);
  }

  public abstract void destroy();

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

  public class DataStorageException extends RuntimeException {
    public DataStorageException() {
    }

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
