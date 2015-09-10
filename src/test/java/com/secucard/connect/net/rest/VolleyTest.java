package com.secucard.connect.net.rest;

import android.app.Activity;
import android.app.Application;
import android.os.AsyncTask;
import com.secucard.connect.SecucardConnect;
import com.secucard.connect.auth.AbstractClientAuthDetails;
import com.secucard.connect.auth.exception.AuthDeniedException;
import com.secucard.connect.auth.model.AppUserCredentials;
import com.secucard.connect.auth.model.ClientCredentials;
import com.secucard.connect.auth.model.OAuthCredentials;
import com.secucard.connect.product.app.SecuAppService;
import com.secucard.connect.product.common.model.ObjectList;
import com.secucard.connect.product.common.model.QueryParams;
import com.secucard.connect.product.loyalty.Loyalty;
import com.secucard.connect.product.loyalty.MerchantCardsService;
import com.secucard.connect.product.loyalty.model.MerchantCard;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.httpclient.FakeHttp;

import java.io.File;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class VolleyTest {
  static SecucardConnect client;

  @Before
  public void before() throws Exception {
    FakeHttp.getFakeHttpLayer().logHttpRequests();
    FakeHttp.getFakeHttpLayer().interceptHttpRequests(false);
    FakeHttp.getFakeHttpLayer().interceptResponseContent(false);

    SecucardConnect.Configuration cfg = SecucardConnect.Configuration.get("volleytestconfig.properties");

    cfg.clientAuthDetails = new AbstractClientAuthDetails("volleyteststore") {

      @Override
      public OAuthCredentials getCredentials() {
        return new AppUserCredentials(getClientCredentials(), "thomas.krauss@posteo.de", "Mwx9vMr2", "device");
      }

      @Override
      public ClientCredentials getClientCredentials() {
        return new ClientCredentials(
            "app.mobile.secucard",
            "576459f04ee8f67f7fcb1cf66416306e64517e01106090edfadbd381f81b58fc");
      }
    };

    cfg.runtimeContext = new Application(){
      @Override
      public File getCacheDir() {
        return new File(".scc-cache");
      }
    };

    client = SecucardConnect.create(cfg);
  }


  @Test
  public void test() throws Exception {
    Robolectric.buildActivity(TestActivity.class).create().get().test();
    Thread.sleep(60000);
  }

  public static class TestActivity extends Activity {
    public void test() throws Exception {
      new AsyncTask(){
        @Override
        protected Object doInBackground(Object[] params) {
          mytest();
          return null;
        }
      }.execute();
    }
  }

  private static void mytest() {
    do {
      try {
        client.open();
        break;
      } catch (AuthDeniedException e) {
        // invalid username or password, let try again
        System.err.println(e.getMessage());
      } catch (Exception e) {
        // all other errors are caused by connection problems, bugs, wrong config etc.
        // not solvable by the user
        e.printStackTrace();
        return;
      }
    } while (true);

    MerchantCardsService service = client.service(Loyalty.Merchantcards);

    try {
      ObjectList<MerchantCard> cards = service.getList(new QueryParams(), null);

      System.out.println(cards);

    } catch (Exception e) {
      System.err.println("Error: ");
      e.printStackTrace();
    } finally {
      client.close();
    }
  }
}
