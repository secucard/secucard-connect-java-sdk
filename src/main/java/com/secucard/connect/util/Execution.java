package com.secucard.connect.util;

import com.secucard.connect.Callback;

/**
 * Supports the execution of code either synchronous or asynchronous - depending if a callback was provided to process
 * the execution result (async in that case).
 * The advantage is the code to execute must only provided once, in the async case all the exceptions are passed to the
 * callback automatically.
 * Creates a simple daemon Thread instead of using some fancier java.util.Executors.
 *
 * @param <T> The execution result type.
 */
public abstract class Execution<T> {

  /**
   * Override with code to execute.
   *
   * @return The execution result.
   */
  protected abstract T execute();

  /**
   * Starts execution.
   * Blocks if no callback was provided, executes in another thread else.
   *
   * @param callback The callback which gets notified when the execution completes or fails. Null if no callback.
   * @return The execution result. Always null if a callback was provided.
   */
  public T start(final Callback<T> callback) {
    if (callback == null) {
      return execute();
    } else {
      Thread t = new Thread() {
        @Override
        public void run() {
          T result;
          try {
            result = Execution.this.execute();
          } catch (Throwable e) {
            callback.failed(e);
            return;
          }
          callback.completed(result);
        }
      };
      t.setDaemon(true);
      t.start();
      return null;
    }
  }

}
