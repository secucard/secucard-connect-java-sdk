package com.secucard.connect;

import com.secucard.connect.auth.CredentialsProvider;
import com.secucard.connect.auth.DefaultTokenStore;
import com.secucard.connect.auth.exception.AuthDeniedException;
import com.secucard.connect.auth.model.AppUserCredentials;
import com.secucard.connect.auth.model.OAuthCredentials;
import com.secucard.connect.product.common.model.ObjectList;
import com.secucard.connect.product.common.model.QueryParams;
import com.secucard.connect.product.general.General;
import com.secucard.connect.product.loyalty.Loyalty;
import com.secucard.connect.product.loyalty.MerchantCardsService;
import com.secucard.connect.product.loyalty.model.MerchantCard;

import java.io.IOException;

public class AppUserDemo {
  public static void main(String[] args) throws IOException {
    new AppUserDemo().test();
  }

  public void test() throws IOException {

    SecucardConnect.Configuration cfg = SecucardConnect.Configuration.get();
    cfg.id = "appusertest";

    cfg.credentialsProvider  = new CredentialsProvider() {
      @Override
      public OAuthCredentials getCredentials() {
        return login();
      }
    };

    cfg.tokenStore = new DefaultTokenStore("appuserdemo-ts");

    SecucardConnect client = SecucardConnect.create(cfg);

    do {
      try {
        client.open();
        break;
      } catch (AuthDeniedException e) {
        // invalid username or password, try again
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
      e.printStackTrace();
    } finally {
      client.close();
    }

  }


  /**
   * Method which simulates requesting login from user.
   */
  private AppUserCredentials login() {
    return new AppUserCredentials(
        "myid",
        "mysecret",
        "myname",
        "mypassword",
        "mydevice");
  }
}
