package com.secucard.connect.storage;

/**
 *
 */
public abstract class DataStorage {

  public abstract void save(String id, Object object, boolean replace) throws DataStorageException;

  public void save(String id, Object object) throws DataStorageException {
    save(id, object, true);
  }

  public abstract <T> T get(String id);

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

    public DataStorageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
      super(message, cause, enableSuppression, writableStackTrace);
    }
  }
}
