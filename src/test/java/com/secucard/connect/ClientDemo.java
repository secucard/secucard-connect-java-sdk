package com.secucard.connect;

import com.secucard.connect.client.Client;
import com.secucard.connect.client.ClientConfiguration;
import com.secucard.connect.client.ClientFactory;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.model.general.Event;
import com.secucard.connect.model.smart.*;

import java.util.List;

public class ClientDemo implements EventListener {

  public static void main(String[] args) throws Exception {

    ClientConfiguration cfg = ClientConfiguration.getDefault();
//    ClientConfiguration cfg = ClientConfiguration.fromProperties(
//        "/home/public/secu/secuconnect/secucard-connect-java-client-lib/src/test/resources/config.properties");

    ClientFactory factory = ClientFactory.getInstance().init(cfg);

    Client client = factory.create(Client.class);

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
