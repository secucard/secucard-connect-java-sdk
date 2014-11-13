package com.secucard.connect.channel.stomp;

import com.secucard.connect.channel.AbstractChannel;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.transport.QueryParams;
import net.jstomplite.Config;
import net.jstomplite.StompClient;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class AutoconnectStompChanel extends AbstractChannel {
  private StompClient stompClient;
  private boolean connected;
  private boolean connectionError;
  private String connectionErrorMsg;
  private String receipt;
  private boolean useReceipt;
  private boolean autoConnect = true;
  private AtomicBoolean isConnected;

  public AutoconnectStompChanel(Configuration cfg) {
    stompClient = new StompClient(new Config(cfg.getHost(), cfg.getPort(), cfg.getVirtualHost(), cfg.getUserId(),
        cfg.getPassword(), cfg.getHeartbeatMs(), cfg.isUseSsl(), cfg.getSocketTimeoutSec())) {

      @Override
      protected void onConnect(Map<String, String> headers) {
        connected = true;
      }

      @Override
      protected void onReceipt(String Id) {
        receipt = Id;
      }

      @Override
      protected void onMessage(String id, String subscription, String destination, Map<String, String> headers, String body) {
        if (isConnectionError(headers, body)) {
          connectionErrorMsg = "connection error";
          connectionError = true;
        }
      }

      @Override
      protected void onError(Map<String, String> headers) {
        if (isConnectionError(headers, null)) {
          connectionErrorMsg = "connection error";
          connectionError = true;
        }
      }

      @Override
      protected void onDisconnect(Exception ex) {
        connected = false;
      }
    };
  }

  public void doSomething(final String id) {
    connectIfNecessary(false);
    realSend(id);
  }

  private synchronized void realSend(final String id) {
    receipt = null;
    stompClient.send("/temp-queue/test", "test", StompClient.createHeader("receipt", id));
    boolean timeout = new SyncWaiter() {
      public boolean stop() {
        return id.equals(receipt);
      }
    }.wait(15);

    if (timeout) {
      throw new RuntimeException("Message receipt time out");
    }
  }

  public synchronized void closeit(boolean disconnect) {
    if (connected) {
      stompClient.close(disconnect);
      connected = false;
    }
  }

  private synchronized void connectIfNecessary(boolean forceConnect) {
    if (connected) {
      return;
    }
    if (!autoConnect && !forceConnect) {
      return;
    }

    connectionError = false;
    connectionErrorMsg = null;
    try {
      stompClient.connect();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    new SyncWaiter() {
      public boolean stop() {
        return connected || connectionError;
      }
    }.wait(15);

    if (connected) {
      return;
    }

    // no connect received at this point - no need to send disconnect
    stompClient.close(false);
    throw new RuntimeException(connectionError ? connectionErrorMsg : "connection timeout");
  }

  private static boolean isConnectionError(Map<String, String> headers, String body) {
    return true;
  }


  private abstract class SyncWaiter {
    boolean wait(int timeout) {
      long maxWait = System.currentTimeMillis() + timeout * 1000;
      while (System.currentTimeMillis() < maxWait) {
        if (stop()) {
          return false;
        }
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          break;
        }
      }
      return true;
    }

    public abstract boolean stop();
  }

  public static void main(String[] args) {
    final AutoconnectStompChanel chanel = new AutoconnectStompChanel(
        new Configuration("localhost", null, 61613, "", null, null, false, false, "", 500, 500, 500, 15, 0));

    try {
//      chanel.open();
      for (int i = 0; i < 30; i++) {
        final int x = i;
        new Thread() {
          @Override
          public void run() {
            chanel.doSomething("xxx" + x);
            chanel.close();
          }
        }.start();
        Thread.sleep(1);
      }
      Thread.sleep(10000);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      chanel.close();
    }
  }

  @Override
  public void open() throws IOException {
    connectIfNecessary(true);
  }

  @Override
  public void close() {
    closeit(true);
  }

  @Override
  public <T extends SecuObject> T getObject(Class<T> type, String objectId) {
    return null;
  }

  @Override
  public <T extends SecuObject> ObjectList<T> findObjects(Class<T> type, QueryParams q) {
    return null;
  }

  @Override
  public <T extends SecuObject> T saveObject(T object) {
    return null;
  }

  @Override
  public <T extends SecuObject> boolean deleteObject(Class<T> type, String objectId) {
    return false;
  }

  @Override
  public void setEventListener(EventListener listener) throws UnsupportedOperationException {

  }

  @Override
  public <A, R> R execute(String action, String[] id, A arg, Class<R> returnType) {
    return null;
  }

  @Override
  public void invoke(String command) {

  }


}
