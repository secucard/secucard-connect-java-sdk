package com.secucard.connect.channel.stomp;

import com.secucard.connect.Callback;
import com.secucard.connect.ClientConfiguration;
import com.secucard.connect.auth.OAuthProvider;
import com.secucard.connect.channel.rest.RestChannel;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.general.Skeleton;
import com.secucard.connect.service.AbstractServicesTest;
import com.secucard.connect.storage.DiskCache;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

public class StompChannelTest {

  private ClientConfiguration clientConfiguration;

  @Before
  public void before() throws Exception {
    AbstractServicesTest.initLogging();
    clientConfiguration = ClientConfiguration.fromProperties("config.properties");
  }

  @Test
  public void run() throws Exception {
    String id = "sct";
    StompChannel sc = new StompChannel(id, clientConfiguration.getStompConfiguration());

    OAuthProvider ap = new OAuthProvider(id, new OAuthProvider.Configuration(clientConfiguration.getOauthUrl(),
        clientConfiguration.getAuthWaitTimeoutSec(), true, null));

    DiskCache storage = new DiskCache("/tmp/sccache/" + id);
    ap.setDataStorage(storage);

    RestChannel rc = new RestChannel(id, clientConfiguration.getRestConfiguration());
    ap.setRestChannel(rc);

    ap.setCredentials(clientConfiguration.getClientCredentials());
    sc.setAuthProvider(ap);

    sc.setEventListener(new EventListener() {
      @Override
      public void onEvent(Object event) {
        System.out.println("EVENT: " + event);
      }
    });

    try {
      sc.findObjects(Skeleton.class, null, new Callback<ObjectList<Skeleton>>() {
        @Override
        public void completed(ObjectList<Skeleton> result) {
          System.out.println("COMPLETED: " + result);
        }

        @Override
        public void failed(Throwable cause) {
          cause.printStackTrace();
        }
      });
      Thread.sleep(10 * 1000);

      // simulating token change an forcing reconnect
      ap.clearCache();
      ObjectList<Skeleton> objects = sc.findObjects(Skeleton.class, null, null);

      Assert.assertNotNull(objects);
    } finally {
      sc.close();
      rc.close();
      storage.destroy();
    }
  }
}
