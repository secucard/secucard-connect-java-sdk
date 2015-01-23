package com.secucard.connect.channel.stomp;

import com.secucard.connect.service.AbstractServicesTest;
import org.junit.Before;

import static org.junit.Assert.assertEquals;

public class Ping extends AbstractServicesTest {
  private StompChannel stompChannel;

  @Before
  public void before() throws Exception {
    super.before();

    stompChannel = new StompChannel("test", clientConfiguration.getStompConfiguration());

    // No auth provider needed when stomp.user + stomp.password is set in config!
    stompChannel.setAuthProvider(null);
  }

  @Override
  public void test() throws Exception {
    try {
      stompChannel.open(null);

      String result = stompChannel.ping();
      assertEquals("pong", result);

    } finally {
      stompChannel.close(null);
    }
  }

  @Override
  protected String getConfigString() {
    return "stomp.user=smart\nstomp.password=smart";
  }
}
