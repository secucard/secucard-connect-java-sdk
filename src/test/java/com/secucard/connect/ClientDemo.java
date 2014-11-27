package com.secucard.connect;

import com.secucard.connect.client.Client;
import com.secucard.connect.client.ClientConfiguration;
import com.secucard.connect.client.ExceptionHandler;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.model.general.skeleton.Skeleton;
import com.secucard.connect.model.smart.*;
import com.secucard.connect.model.transport.QueryParams;
import com.secucard.connect.service.general.GeneralService;
import com.secucard.connect.service.smart.SmartService;

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
    final Client client = Client.create(id, cfg);
    client.setEventListener(new EventListener() {
      @Override
      public void onEvent(Object event) {
        System.out.println("Event: " + event);
      }
    });

    client.setExceptionHandler(new ExceptionHandler() {
      @Override
      public void handle(Exception exception) {
        client.disconnect();
        exception.printStackTrace();
      }
    });

    client.connect();

    GeneralService generalService = client.getService(GeneralService.class);
    QueryParams queryParams = new QueryParams();
    queryParams.setOffset(1);
    queryParams.setCount(10);
    queryParams.setFields("a", "b", "c");
    queryParams.addSortOrder("a", QueryParams.SORT_ASC);
    queryParams.addSortOrder("b", QueryParams.SORT_DESC);
    queryParams.setQuery("a:abc1? OR (b:*0 AND NOT c:???1??)");
    List<Skeleton> skeletons = generalService.getSkeletons(queryParams);


    SmartService smartService = client.getService(SmartService.class);
    // or get by a defined name:
    // SmartService smartService = client.getService("smart");

    // in production id would be the vendor uuid,
    Device device = new Device(id);
    boolean ok = smartService.registerDevice(device);
    if (!ok) {
      throw new RuntimeException("Error registering device.");
    }


    // select an ident
    List<Ident> availableIdents = smartService.getIdents();
    if(availableIdents == null) {
      throw new RuntimeException("No idents found.");
    }
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

    String type = "demo"; // demo|auto|cash
    // demo instructs the server to simulate a different (random) transaction for each invocation of startTransaction

    Result result = smartService.startTransaction(transaction, type);

    client.disconnect();

    System.out.println("Transaction finished: " + result);
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
