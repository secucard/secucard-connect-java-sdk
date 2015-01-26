package com.secucard.connect;

/**
 * Implementing  instances can be passed to methods to get asyncronous notified when the
 * operation performed by the method completes or failes.
 *
 * @param <T> The type of the operations result.
 */
public interface Callback<T> {

  /**
   * Gets called when the operation is sucessfully completed.
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
}
