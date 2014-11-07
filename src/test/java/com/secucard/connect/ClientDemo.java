package com.secucard.connect;

import com.secucard.connect.auth.OAuthClientCredentials;
import com.secucard.connect.model.general.Event;
import com.secucard.connect.model.smart.*;
import com.secucard.connect.rest.RestConfig;
import com.secucard.connect.stomp.StompConfig;

import java.util.List;

public class ClientDemo implements EventListener {

  public static void main(String[] args) {

    OAuthClientCredentials clientCredentials = new OAuthClientCredentials(
        "webapp", "821fc7042ec0ddf5cc70be9abaa5d6d311db04f4679ab56191038cb6f7f9cb7c");

    ClientConfig config = new ClientConfig(
        new RestConfig("https://core-dev10.secupay-ag.de/app.core.connector/api/v2",
            "https://core-dev10.secupay-ag.de/app.core.connector/oauth/token", clientCredentials),
        new StompConfig("dev10.secupay-ag.de", null, 61614, "/exchange/connect.api/", "guest", "guest", 0, true, true,
            "/temp-queue/main", 20, 60, 360, 5));

    Client client = Client.create(config);

    client.setEventListener(new ClientDemo());

    try {
      client.connect();

      Device device = new Device("me", "cashier");
      client.registerDevice(device);

      Basket basket = new Basket();
      BasketInfo basketInfo = new BasketInfo(19.99f, "euro");
      Transaction transaction = new Transaction();
      transaction.setBasket(basket);
      transaction.setBasketInfo(basketInfo);

      List<Ident> idents = client.getIdents();
      transaction.setIdents(idents);

      transaction = client.createTransaction(transaction);

      Result result = client.startTransaction(transaction);

      System.out.println("Transaction finished: " + result);

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      client.disconnect();
    }

  }


  @Override
  public void onEvent(Event event) {
    System.out.println("Event: " + event);
  }
}
