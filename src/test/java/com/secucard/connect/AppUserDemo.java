package com.secucard.connect;

import com.secucard.connect.auth.AppUserCredentials;
import com.secucard.connect.auth.AuthException;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.QueryParams;
import com.secucard.connect.model.loyalty.MerchantCard;
import com.secucard.connect.service.loyalty.MerchantCardsService;

import java.io.IOException;

public class AppUserDemo {
  public static void main(String[] args) throws IOException {
    new AppUserDemo().test();
  }

  public void test() throws IOException {

    Client client = Client.create("appusertest", "config-appuserdemo.properties");

    try {
      client.connect();
    } catch (AuthException e) {
      // means no token yet or could not refresh, however we need new authentication
      while (true) {
        try {
          client.connect(login(), null);
          break;
        } catch (AuthException auth) {
          if (auth.getError().equals(AuthException.ERROR_INVALID_GRANT)) {
            // just username or password wrong, try again
            continue;
          }
        }
        // all other errors probably not recoverable by the user except connection problems maybe
        // best solution would be to present to the user and let him decide
        return;
      }
    } catch (Exception e) {
      // bug or connection problem
      e.printStackTrace();
      return;
    }

    MerchantCardsService service = client.getService(MerchantCardsService.class);

    try {
      ObjectList<MerchantCard> cards = service.getMerchantCards(new QueryParams(), null);

      System.out.println(cards);

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      client.disconnect();
    }

  }


  /**
   * Method which simulates requesting login from user.
   */
  private AppUserCredentials login() {
    return new AppUserCredentials(
        "app.mobile.secucard",
        "576459f04ee8f67f7fcb1cf66416306e64517e01106090edfadbd381f81b58fc",
        "user",
        "pwd",
        "tk1");
  }
}
