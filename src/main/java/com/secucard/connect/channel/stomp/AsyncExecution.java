package com.secucard.connect.channel.stomp;

abstract class AsyncExecution {
  protected abstract void run();

  public void start() {
    new Thread(new Runnable() {
      @Override
      public void run() {
        AsyncExecution.this.run();
      }
    }).start();
  }
}
