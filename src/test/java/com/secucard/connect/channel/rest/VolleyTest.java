package com.secucard.connect.channel.rest;

import android.app.Application;
import com.secucard.connect.Callback;
import com.secucard.connect.Client;
import com.secucard.connect.ClientConfiguration;
import com.secucard.connect.auth.AuthProvider;
import com.secucard.connect.auth.UserCredentials;
import com.secucard.connect.model.general.skeleton.Skeleton;
import com.secucard.connect.model.transport.QueryParams;
import com.secucard.connect.service.AbstractServicesTest;
import com.secucard.connect.service.TestService;
import com.secucard.connect.storage.MemoryDataStorage;
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
  private VolleyChannel channel;

  @Before
  public void before() throws Exception {
    Robolectric.getFakeHttpLayer().interceptHttpRequests(false);
    Robolectric.getFakeHttpLayer().interceptResponseContent(false);

    initLogging();

    clientConfiguration = ClientConfiguration.fromProperties("volley-config.properties");
    clientConfiguration.setUserCredentials(new UserCredentials("checkout@secucard.com", "checkout"));
    client = Client.create("test", clientConfiguration, new Application());
    context = client.getService(TestService.class).getContext();

    Configuration configuration = clientConfiguration.getRestConfiguration();

    channel = (VolleyChannel) context.getRestChannel();
    channel = new VolleyChannel("", new Application(), configuration);
    OAuthProvider authProvider = (OAuthProvider) context.getAuthProvider();
    authProvider.setRestChannel(channel);
    channel.setAuthProvider(authProvider);
  }


  private Configuration addProxy(Configuration configuration) {
    // replace original host with intercepting proxy
    String host = "http://localhost:4444/";
    configuration = new Configuration(host + configuration.getBaseUrl().split("/", 4)[3]);
    return configuration;
  }

  @Override
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
