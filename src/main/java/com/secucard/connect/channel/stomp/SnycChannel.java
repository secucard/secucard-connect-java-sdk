package com.secucard.connect.channel.stomp;

import net.jstomplite.Config;
import net.jstomplite.StompClient;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;

public class SnycChannel {
  private final StompClient stompClient;
  private Set<String> receipts = new ConcurrentSkipListSet<>();
  private AtomicBoolean isConnected = new AtomicBoolean(false);


  public SnycChannel(Configuration cfg) {
    stompClient = new StompClient(new Config(cfg.getHost(), cfg.getPort(), cfg.getVirtualHost(), cfg.getUserId(),
        cfg.getPassword(), cfg.getHeartbeatMs(), cfg.isUseSsl(), cfg.getSocketTimeoutSec())) {
      @Override
      protected void onConnect(Map<String, String> headers) {
        isConnected.set(true);
      }

      @Override
      protected void onReceipt(String Id) {
        if (!receipts.add(Id)) {
          throw new RuntimeException("duplicate receipt, not added");
        }
      }

      @Override
      protected void onMessage(String id, String subscription, String destination, Map<String, String> headers, String body) {

      }

      @Override
      protected void onError(Map<String, String> headers) {

      }

      @Override
      protected void onDisconnect(Exception ex) {
        isConnected.set(false);
      }
    };
  }

  public void connect(String user) {
    if (!isConnected.get()) {
      try {
        stompClient.connect(user, null);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
      awaitConnect();
    }
  }

  public void send(final String id) {
    Map<String, String> headers = null;
    headers = StompClient.createHeader("receipt", id);
    synchronized (this) {
      connect(id);
      stompClient.send("/temp-queue/test", "" + id, headers);
    }
    awaitReceipt(id);
  }

  public void close() {
    synchronized (this) {
      if (isConnected.compareAndSet(true, false)) {
        stompClient.close();
      }
    }
  }

  private void awaitConnect() {
    if (new SyncWaiter() {
      @Override
      public boolean stop() {
        return isConnected.get();
      }
    }.wait(10, 1)) {
      throw new RuntimeException("connection timeout");
    }
  }

  private void awaitReceipt(final String id) {
    if (new SyncWaiter() {
      @Override
      public boolean stop() {
        return receipts.remove(id);
      }
    }.wait(10, 1)) {
      throw new RuntimeException("no receipt");
    }
  }

  public static void main(String[] args) {
    run();
  }

  private  static void run() {
    final SnycChannel chanel = new SnycChannel(
        new Configuration("localhost", null, 61613, "", null, null, false, false, "", 500, 500, 500, 15, 0));

    try {
//      chanel.connect("");
      for (int i = 0; i < 100; i++) {
        final int x = i;
//        final int r = (int) (Math.random() * 100);
        new Thread() {
          @Override
          public void run() {
//            chanel.connect("" + x);
            chanel.send("" + x);
            if (x % 3 == 0) {
              chanel.close();
            }
          }
        }.start();
        Thread.sleep(1);
      }
      Thread.sleep(5000);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      chanel.close();
    }
  }

  private abstract class SyncWaiter {
    boolean wait(int timeoutSec, int sleepMs) {
      long maxWait = System.currentTimeMillis() + timeoutSec * 1000;
      while (System.currentTimeMillis() < maxWait) {
        if (stop()) {
          return false;
        }
        try {
          Thread.sleep(sleepMs);
        } catch (InterruptedException e) {
          break;
        }
      }
      return true;
    }

    public abstract boolean stop();
  }

}
