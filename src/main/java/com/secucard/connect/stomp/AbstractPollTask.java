package com.secucard.connect.stomp;

import java.util.concurrent.*;

abstract class AbstractPollTask<T> {
  private FutureTask<T> task;

  AbstractPollTask() {
    task = new FutureTask<T>(new Callable<T>() {
      @Override
      public T call() throws Exception {
        return AbstractPollTask.this.call();
      }
    });
  }

  public T get(int timeout) throws ExecutionException {
    Thread thread = new Thread(task);
    thread.start();
    try {
      return task.get(timeout, TimeUnit.SECONDS);
    } catch (InterruptedException | TimeoutException e) {
      // ignore
    } finally {
      // finish thread
      thread.interrupt();
      try {
        thread.join();
      } catch (InterruptedException e) {
      }
    }
    return null;
  }

  protected abstract T call();
}
