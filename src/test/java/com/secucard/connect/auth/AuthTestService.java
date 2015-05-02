package com.secucard.connect.auth;

import com.secucard.connect.ClientConfiguration;
import com.secucard.connect.channel.rest.RestChannelBase;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.service.TestService;
import com.secucard.connect.storage.DataStorage;
import com.secucard.connect.storage.DiskCache;
import junit.framework.Assert;


public class AuthTestService extends TestService implements EventListener {

  public void deviceAuth() throws Exception {
    MyOAuthProvider ap = new MyOAuthProvider();
    ClientConfiguration config = context.getConfig();
    ap.setCredentials(new DeviceCredentials(config.getClientCredentials().getClientId(), config.getClientCredentials().getClientSecret(), config.getDeviceId()));
    ap.clearToken();
    String  token = ap.getToken(false);
    Assert.assertNotNull(token);
  }

  public void deviceAuthStepped() throws Exception {
    MyOAuthProvider ap = new MyOAuthProvider();
    getRestChannel().open();
//    ap.test();
  }

  public void clientIdAuth() throws Exception {

    getRestChannel().open();
    MyOAuthProvider ap = new MyOAuthProvider();
    ClientCredentials clientCredentials = context.getConfig().getClientCredentials();
    ap.setCredentials(clientCredentials);
    String  token = ap.getToken(false);
//    ap.setCredentials(new ClientCredentials(clientCredentials.getClientId(), clientCredentials.getClientSecret() + "_"));
//    token = ap.getToken();
    Assert.assertNotNull(token);
  }


  private class MyOAuthProvider extends OAuthProvider {
    public MyOAuthProvider() throws Exception {
      super("test", new OAuthProvider.Configuration(context.getConfig().getOauthUrl(),
          context.getConfig().getAuthWaitTimeoutSec(), true, null));
      setRestChannel((RestChannelBase) getRestChannel());
      DataStorage storage = new DiskCache("sccache-authtest");
      setDataStorage(storage);
      registerEventListener(AuthTestService.this);
    }

    /*public void test() {
      DeviceAuthCode codes = requestCodes(c);
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
    }*/
  }

  @Override
  public void onEvent(Object event) {
    System.out.println("### Got event: " + event);
  }
}
