package com.secucard.connect;

import com.secucard.connect.client.Client;
import com.secucard.connect.client.ClientConfiguration;
import com.secucard.connect.client.smart.SmartService;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.model.general.Event;
import com.secucard.connect.model.smart.*;

import java.util.Arrays;
import java.util.List;

public class ClientDemo {

  public static void main(String[] args) throws Exception {

    final ClientConfiguration cfg = ClientConfiguration.fromProperties("config.properties");
    // or use default: ClientConfiguration.getDefault();

    process("device1", cfg);

    // or parallel clients
    //runThreaded(cfg);
  }

  private static void process(final String id, ClientConfiguration cfg) {
    Client client = Client.create(id, cfg);
    SmartService smartService = client.createService(SmartService.class);

    try {
      client.connect();

      smartService.setEventListener(new EventListener() {
        @Override
        public void onEvent(Event event) {
          System.out.println("Event for " + id + ": " + event);
        }
      });

      // in production id would be the vendor uuid,
      Device device = new Device(id);
      boolean ok = smartService.registerDevice(device);
      if (!ok) {
        throw new RuntimeException("Error registering device.");
      }


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

      Transaction newTrans = new Transaction(device.getId(), basketInfo, basket, selectedIdents);

      Transaction transaction = smartService.createTransaction(newTrans);

      Result result = smartService.startTransaction(transaction);

      System.out.println("Transaction finished: " + result);


    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      client.disconnect();
    }
  }

  private static void runThreaded(final ClientConfiguration cfg) {
    for (int i = 0; i < 1; i++) {
      final String id = "device" + i;
      new Thread() {
        @Override
        public void run() {
          process(id, cfg);
        }
      }.start();
    }
  }
}
