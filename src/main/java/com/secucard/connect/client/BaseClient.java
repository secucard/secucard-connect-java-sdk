package com.secucard.connect.client;

import com.secucard.connect.event.EventListener;

import java.io.IOException;

public class BaseClient {
  private Thread heartbeatInvoker;
  protected ClientContext context;

  public void setContext(ClientContext context) {
    this.context = context;
  }

  public ClientContext getContext() {
    return context;
  }

  public void setEventListener(EventListener eventListener) {
    context.getStompChannel().setEventListener(eventListener);
  }

  public void connect() throws ConnectException {
    try {
      // first rest since it does auth
      context.getRestChannel().open();
      context.getStompChannel().open();
      startHeartBeat();
    } catch (IOException e) {
      throw new ConnectException(e);
    }
  }

  public void disconnect() {
    stopHeartBeat();
    context.getStompChannel().close();
    context.getRestChannel().close();
  }

  public void startHeartBeat() {
    stopHeartBeat();
    heartbeatInvoker = new Thread() {
      @Override
      public void run() {
        while (!isInterrupted()) {
          context.getStompChannel().invoke("ping");
          try {
            Thread.sleep(context.getConfig().getHeartBeatSec() * 1000);
          } catch (InterruptedException e) {
            break;
          }
        }
      }
    };
    heartbeatInvoker.start();
  }

  public void stopHeartBeat() {
    if (heartbeatInvoker != null && heartbeatInvoker.isAlive()) {
      heartbeatInvoker.interrupt();
      try {
        heartbeatInvoker.join();
      } catch (InterruptedException e) {
      }
    }
  }
}
