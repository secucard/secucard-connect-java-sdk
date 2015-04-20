package com.secucard.connect.channel.stomp;

public abstract class AsyncExecution {
  protected abstract void run();

  public void start() {
    Thread thread = new Thread(new Runnable() {
      @Override
      public void run() {
        AsyncExecution.this.run();
      }
    });
    thread.setDaemon(true);
    thread.start();
  }
}
