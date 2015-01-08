package com.secucard.connect;

import com.secucard.connect.auth.AuthProvider;
import com.secucard.connect.channel.stomp.StompChannel;
import junit.framework.Assert;
import org.junit.Before;

public class StompPing {
  private StompChannel stompChannel;
  private String result;

  @Before
  public void before() throws Exception {
    ClientConfiguration cfg = ClientConfiguration.fromProperties("config.properties");
    AuthProvider authProvider;

    stompChannel = new StompChannel("test", cfg.getStompConfiguration());

    authProvider = null; // No auth provider needed when stomp.user + stomp.password is set in config!

    stompChannel.setAuthProvider(authProvider);
  }

  @org.junit.Test
  public void run() throws Exception {
    try {
      stompChannel.open(null);

      result = null;
      pingasync();
      Assert.assertEquals("pong", result);

      result = null;
      ping(null);
      Assert.assertEquals("pong", result);

    } finally {
      stompChannel.close(null);
    }
  }

  private void ping(Callback<String> callback) {
    result = stompChannel.invoke("ping", callback);
  }

  private void pingasync() throws InterruptedException {
    ping(new Callback<String>() {
      @Override
      public void completed(String res) {
        result = res;
      }

      @Override
      public void failed(Throwable throwable) {
        throwable.printStackTrace();
      }
    });
    Thread.sleep(3000); // need to wait a bit when using callback
  }
}
