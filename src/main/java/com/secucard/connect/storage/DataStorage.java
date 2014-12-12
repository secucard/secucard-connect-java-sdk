package com.secucard.connect.storage;

/**
 * Gives basic access to the data store implementation.
 * Used to persist any data, like acess tokens.
 */
public abstract class DataStorage {

  /**
   * Save a object for an id.
   *
   * @param id
   * @param object
   * @param replace True if a existing object of same id should be overwritten, false else.
   * @throws DataStorageException
   */
  public abstract void save(String id, Object object, boolean replace) throws DataStorageException;

  public void save(String id, Object object) throws DataStorageException {
    save(id, object, true);
  }

  public abstract <T> T get(String id);

  /**
   * Remove all data for a given id.
   * The id may contain a wildcard sign "*", in this case all matching entries are removed
   */
  public abstract void clear(String id);

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
