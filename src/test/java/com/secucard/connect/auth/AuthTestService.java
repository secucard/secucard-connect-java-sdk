package com.secucard.connect.auth;

import com.secucard.connect.channel.rest.OAuthProvider;
import com.secucard.connect.channel.rest.RestChannelBase;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.model.auth.DeviceAuthCode;
import com.secucard.connect.model.auth.Token;
import com.secucard.connect.service.TestService;
import com.secucard.connect.storage.DataStorage;
import com.secucard.connect.storage.SimpleFileDataStorage;
import junit.framework.Assert;
import org.junit.Assume;


public class AuthTestService extends TestService implements EventListener {

  public void deviceAuth() throws Exception {
    MyOAuthProvider ap = new MyOAuthProvider();
    getRestChannel().open(null);
    Token token = ap.getToken();
    Assert.assertNotNull(token);
  }

  public void deviceAuthStepped() throws Exception {
    MyOAuthProvider ap = new MyOAuthProvider();
    getRestChannel().open(null);
    ap.test();
  }

  public void clientIdAuth() throws Exception {
    getRestChannel().open(null);
    Token token = new MyOAuthProvider().getToken(false);
    Assert.assertNotNull(token);
  }




  private class MyOAuthProvider extends OAuthProvider {
    public MyOAuthProvider() throws Exception {
      super("test", context.getConfig());
      setRestChannel((RestChannelBase) getRestChannel());
      DataStorage storage = new SimpleFileDataStorage("sccache");
      setDataStorage(storage);
      registerEventListener(AuthTestService.this);
    }

    public void test() {
      DeviceAuthCode codes = requestCodes();
      Assert.assertNotNull(codes);
      System.out.println("### Got code: " + codes);
      Token token = null;
      try {
        token = pollToken(codes);
      } catch (Exception e) {
        Assume.assumeNoException(e);
      }
      Assert.assertNotNull(token);
      System.out.println("### Got token: " + token);
    }
  }

  @Override
  public void onEvent(Object event) {
    System.out.println("### Got event: " + event);
  }
}
