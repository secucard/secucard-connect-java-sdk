package com.secucard.connect.channel.rest;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import com.secucard.connect.Callback;
import com.secucard.connect.Client;
import com.secucard.connect.ClientConfiguration;
import com.secucard.connect.auth.OAuthProvider;
import com.secucard.connect.model.QueryParams;
import com.secucard.connect.model.general.Skeleton;
import com.secucard.connect.service.AbstractServicesTest;
import com.secucard.connect.service.TestService;
import com.secucard.connect.storage.DiskCache;
import junit.framework.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.InputStream;

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
//    clientConfiguration.setUserCredentials(new UserCredentials("checkout@secucard.com", "checkout"));
    client = Client.create("test", clientConfiguration, new Application(), null);
    context = client.getService(TestService.class).getContext();

    // customize rest channel
    channel = new TestVolleyChannel(context.getClientId(), (Context) context.getRuntimeContext(),
        clientConfiguration.getRestConfiguration());
    OAuthProvider authProvider = (OAuthProvider) context.getAuthProvider();
    authProvider.setRestChannel(channel);
    authProvider.setDataStorage(new DiskCache("/home/public/projects/secu/secuconnect/SecuConnect/sccache/test"));
    channel.setAuthProvider(authProvider);
  }

  @Override
  public void test() throws Exception {
    Robolectric.buildActivity(TestActivity.class).create().get().test();
  }

  public static class TestActivity extends Activity {
    public void test() throws Exception {
      try {
        channel.open();
        String url = "https://connect.secucard.com/ds_g/dda378c077394ce074a5c0841ab4c44c4cda608d";
        InputStream stream = channel.getStream(url, null, null, null);

//        Object post = channel.post(url, null, null, null, 404);

//        Object post = channel.post(url, null, null, null, 404);

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
        queryParams.setOffset(-2);
        queryParams.setCount(10);
        queryParams.setQuery("id:12345");
        channel.getList(Skeleton.class, queryParams, callback);

//      channel.getObject(Skeleton.class, "skl_60", callback);

//      channel.createObject(new Skeleton(), callback);

//      channel.updateObject(Account.class, "me", "location", null, new Location(), Result.class, callback);

//      channel.execute(Transaction.class, "21", "start", "demo", null, TransactionResult.class, callback);

//      channel.execute("21", "start", null, Result.class, callback);

        Thread.sleep(10000);

      } finally {
        channel.close();
      }
    }
  }

}
