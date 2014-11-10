package com.secucard.connect;

import com.secucard.connect.client.Client;
import com.secucard.connect.client.ClientConfiguration;
import com.secucard.connect.client.smart.SmartService;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.model.general.Event;
import com.secucard.connect.model.smart.*;

import java.util.Arrays;
import java.util.List;

public class ClientDemo implements EventListener {

  public static void main(String[] args) throws Exception {

//    ClientConfiguration cfg = ClientConfiguration.getDefault();
    process1(ClientConfiguration.fromProperties("config.properties"));

//    process2(ClientConfiguration.fromProperties("config.properties"));

  }

  private static void process1(ClientConfiguration cfg) {
    Client client = Client.create("client1", cfg);
    SmartService smartService = client.createService(SmartService.class);

    try {
      client.connect();

      smartService.setEventListener(new EventListener() {
        @Override
        public void onEvent(Event event) {
          System.out.println("Event for client 1: " + event);
        }
      });

      String deviceId = "me";
      Device device = new Device(deviceId, "cashier");
      smartService.registerDevice(device);


      // select an ident
      List<Ident> availableIdents = smartService.getIdents();
      Ident ident = Ident.find("smi_1", availableIdents);
      ident.setValue("pdo28hdal");

      List<Ident> selectedIdents = Arrays.asList(ident);

      Basket basket = new Basket();
      basket.addProduct(new Product("art1", "3378", "5060215249804", "desc1", 5.f, 19.99f, 19));
      basket.addProduct(new Product("art2", "34543", "5060215249805", "desc2", 1.5f, 9.99f, 2));
      basket.addProduct(new Text("art2", "text1"));
      basket.addProduct(new Text("art2", "text2"));
      basket.addProduct(new Product("art2", "08070", "60215249807", "desc3", 20f, 2.19f, 50f));

      BasketInfo basketInfo = new BasketInfo(136.50f, BasketInfo.getEuro());

      Transaction newTrans = new Transaction(deviceId, basketInfo, basket, selectedIdents);

      Transaction transaction = smartService.createTransaction(newTrans);

      Result result = smartService.startTransaction(transaction);

      System.out.println("Transaction finished: " + result);



    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      client.disconnect();
    }
  }

  private static void process2(ClientConfiguration cfg) {
    Client client = Client.create("client2", cfg);
    client.setEventListener(new ClientDemo());
    SmartService smartService = client.createService(SmartService.class);

    try {
      client.connect();

      Device device = new Device("me", "cashier");
      smartService.registerDevice(device);

      Basket basket = new Basket();
      BasketInfo basketInfo = new BasketInfo(19.99f, BasketInfo.getEuro());
      Transaction transaction = new Transaction();
      transaction.setBasket(basket);
      transaction.setBasketInfo(basketInfo);

      List<Ident> idents = smartService.getIdents();

      transaction.setIdents(Arrays.asList(new Ident("", "")));

      transaction = smartService.createTransaction(transaction);

      Result result = smartService.startTransaction(transaction);

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
