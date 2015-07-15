package com.secucard.connect.client;

/**
 * Implementing instances can be passed to methods to get asynchronous notified when the
 * operation performed by the method completes or fails.
 *
 * @param <T> The type of the operations result.
 */
public interface Callback<T> {

  /**
   * Gets called when the operation is successfully completed.
   *
   * @param result The value returned by the operation.
   */
  void completed(T result);

  /**
   * Gets called when the operation has failed.
   *
   * @param cause The fail cause.
   */
  void failed(Throwable cause);

  /**
   * Callback which combines result and failure notification in one method.
   *
   * @param <T> The result type.
   */
  public static abstract class Simple<T> implements Callback<T> {

    @Override
    public void completed(T result) {
      completed(result, null);
    }
    @Override
    public void failed(Throwable cause) {
      completed(null, cause);
    }

    public abstract void completed(T result, Throwable error);

  }
  /**
   * Callback which has no failure notification.
   *
   * @param <T>
   */
  public static interface Notify<T> {
    void notify(T result);
  }
}
