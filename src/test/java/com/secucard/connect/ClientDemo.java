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
        System.out.println("Got event: " + event);
      }
    });

    // set an optional global exception handler - all exceptions thrown by service methods end up here
    // if not set each method throws as usual, its up to the developer to catch accordingly
    // if callback are used all exceptions go to the failed method
    client.setExceptionHandler(new ExceptionHandler() {
      @Override
      public void handle(Exception exception) {
        System.err.println("Error happened:");
        exception.printStackTrace();
        client.disconnect();
      }
    });

    client.connect();

    // simple retrieval ------------------------------------------------------------------------------------------------

    final GeneralService generalService = client.getService(GeneralService.class);

    // get skeleton, without/with callback
    Skeleton skeleton = generalService.getSkeleton("skl_60", null);
    System.out.println("got skeleton: " + skeleton);

    skeleton = generalService.getSkeleton("skl_60", new Callback<Skeleton>() {
      @Override
      public void completed(Skeleton result) {
        System.out.println("got skeleton: " + result);
      }

      @Override
      public void failed(Throwable throwable) {
        System.err.println("Error retrieving skeleton.");
        throwable.printStackTrace();
      }
    });

    // find skeleton, with callback
    QueryParams queryParams = new QueryParams();
    queryParams.setOffset(1);
    queryParams.setCount(2);
    //queryParams.setFields("a"); seems not to work properly
    queryParams.addSortOrder("a", QueryParams.SORT_ASC);
    queryParams.addSortOrder("b", QueryParams.SORT_DESC);
    queryParams.setQuery("a:abc1? OR (b:*0 AND NOT c:???1??)");
    List<Skeleton> skeletons = generalService.getSkeletons(null, new Callback<List<Skeleton>>() {
      @Override
      public void completed(List<Skeleton> result) {
        System.out.println("got skeletons: " + result);
      }

      @Override
      public void failed(Throwable throwable) {
        System.err.println("Error retrieving skeletons.");
        throwable.printStackTrace();
      }
    });

    // do a smart transaction  ----------------------------------------------------------------------------------------

    SmartService smartService = client.getService(SmartService.class);
    // or get by a defined name:
    // SmartService smartService = client.getService("smart");

    // in production id would be the vendor uuid,
    Device device = new Device(id);
    boolean ok = smartService.registerDevice(device, null);
    if (!ok) {
      client.disconnect();
      throw new RuntimeException("Error registering device.");
    }


    // select an ident
    List<Ident> availableIdents = smartService.getIdents(null);
    if (availableIdents == null) {
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

    Transaction transaction = smartService.createTransaction(newTrans, null);

    String type = "demo"; // demo|auto|cash
    // demo instructs the server to simulate a different (random) transaction for each invocation of startTransaction

    Result result = smartService.startTransaction(transaction, type, null);
    System.out.println("Transaction finished: " + result);

    client.disconnect();

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
