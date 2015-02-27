package com.secucard.connect.channel.rest;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import com.secucard.connect.Callback;
import com.secucard.connect.Client;
import com.secucard.connect.ClientConfiguration;
import com.secucard.connect.auth.UserCredentials;
import com.secucard.connect.model.general.Skeleton;
import com.secucard.connect.model.QueryParams;
import com.secucard.connect.service.AbstractServicesTest;
import com.secucard.connect.service.TestService;
import junit.framework.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class VolleyTest extends AbstractServicesTest {
  private static VolleyChannel channel;

  @Before
  public void before() throws Exception {
    Robolectric.getFakeHttpLayer().logHttpRequests();
    Robolectric.getFakeHttpLayer().interceptHttpRequests(false);
    Robolectric.getFakeHttpLayer().interceptResponseContent(false);

    initLogging();

    clientConfiguration = ClientConfiguration.fromProperties("config-volley.properties");
    clientConfiguration.setUserCredentials(new UserCredentials("checkout@secucard.com", "checkout"));
    client = Client.create("test", clientConfiguration, new Application(), null);
    context = client.getService(TestService.class).getContext();

    // customize rest channel
    channel = new TestVolleyChannel(context.getClientId(), (Context) context.getRuntimeContext(),
        clientConfiguration.getRestConfiguration());
    OAuthProvider authProvider = (OAuthProvider) context.getAuthProvider();
    authProvider.setRestChannel(channel);
    channel.setAuthProvider(authProvider);
  }

  @Override
  public void test() throws Exception {
    Robolectric.buildActivity(TestActivity.class).create().get().test();
  }

  public static class TestActivity extends Activity {
    public void test() throws Exception {
      try {
        channel.open(null);

        Callback callback = new Callback() {
          @Override
          public void completed(Object result) {
            Assert.assertNotNull(result);
          }

          @Override
          public void failed(Throwable throwable) {
            Assume.assumeNoException(throwable);
          }
        };

        QueryParams queryParams = new QueryParams();
        queryParams.setOffset(2);
        queryParams.setCount(10);
        channel.findObjects(Skeleton.class, queryParams, callback);

//      channel.getObject(Skeleton.class, "skl_60", callback);

//      channel.createObject(new Skeleton(), callback);

//      channel.updateObject(Account.class, "me", "location", null, new Location(), Result.class, callback);

//      channel.execute(Transaction.class, "21", "start", "demo", null, TransactionResult.class, callback);

//      channel.execute("21", "start", null, Result.class, callback);

        Thread.sleep(3000);

      } finally {
        channel.close(null);
      }
    }
  }

}
