package com.secucard.connect;

import com.secucard.connect.auth.AuthProvider;
import com.secucard.connect.channel.stomp.StompChannel;
import com.secucard.connect.service.AbstractServicesTest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeNoException;

public class StompPing extends AbstractServicesTest {
  private StompChannel stompChannel;

  @Before
  public void before() throws Exception {
    super.before();
    AuthProvider authProvider;

    stompChannel = new StompChannel("test", clientConfiguration.getStompConfiguration());

    authProvider = null; // No auth provider needed when stomp.user + stomp.password is set in config!

    stompChannel.setAuthProvider(authProvider);
  }

  @Override
  public void test() throws Exception {
    try {
      stompChannel.open(null);

      pingasync();

      String result = ping(null);
      assertEquals("pong", result);

    } finally {
      stompChannel.close(null);
    }
  }

  private String ping(Callback<String> callback) {
    return stompChannel.invoke("ping", callback);
  }

  private void pingasync() throws InterruptedException {
    ping(new Callback<String>() {
      @Override
      public void completed(String res) {
        assertEquals("pong", res);
      }

      @Override
      public void failed(Throwable throwable) {
        assumeNoException(throwable);
      }
    });
    Thread.sleep(3000); // need to wait a bit when using callback
  }
}
