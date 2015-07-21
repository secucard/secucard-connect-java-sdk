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
