package com.secucard.connect;

import com.secucard.connect.event.EventListener;
import com.secucard.connect.model.MediaResource;
import com.secucard.connect.model.QueryParams;
import com.secucard.connect.model.auth.DeviceAuthCode;
import com.secucard.connect.model.general.Skeleton;
import com.secucard.connect.model.smart.*;
import com.secucard.connect.service.general.SkeletonService;
import com.secucard.connect.service.smart.CheckinService;
import com.secucard.connect.service.smart.IdentService;
import com.secucard.connect.service.smart.TransactionService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class ClientDemo2 {

  public static void main(String[] args) throws Exception {

    final ClientConfiguration cfg = ClientConfiguration.fromProperties("config-clientdemo.properties");
    // or use default: ClientConfiguration.getDefault();

    process("device1", cfg);

    // or parallel clients
    //runThreaded(cfg);
  }

  private static void process(final String id, ClientConfiguration cfg) throws Exception {
    final Client client = Client.create(id, cfg);
    // Android usage (passing android.content.Context): Client.create(id, cfg, getApplication());
    // must additionally set androidMode=true in configuration file


    for (int i = 0; i < 3; i++) {
      MediaResource mr = new MediaResource("http://media2.govtech.com/images/770*1000/dog_flickr.jpg?1&2&3");
      long t = System.currentTimeMillis();
      mr.download();
      System.out.println(System.currentTimeMillis() - t);
      t = System.currentTimeMillis();
//      BufferedImage image = ImageIO.read(new FileInputStream("/home/public/projects/secu/secuconnect/SecuConnect/sccache/test/httpsconnectsecucardcomds_g06a1c868bc7f8592c544dab5f7573697ba2b8241"));
      BufferedImage image = ImageIO.read(mr.getInputStream());
      System.out.println(System.currentTimeMillis() - t);
    }

    // set up event listener, especially required to handle auth events!
    client.onEvent(new EventListener() {
      @Override
      public void onEvent(Object event) {
        System.out.println("Got event: " + event);

        if (event instanceof DeviceAuthCode) {
          // device code retrieved successfully - present this data to user
          // user must visit URL in DeviceAuthCode.verificationUrl and must enter codes
          // client polls auth server in background meanwhile until success or timeout (config: auth.waitTimeoutSec )
        }

        if ("AUTH_PENDING".equals(event)) {
          // present to the user - this event comes up periodically as long the authentication is not performed
        }

        if ("AUTH_OK".equals(event)) {
          // present to the user - user has device codes codes typed in and the auth was successfully
        }
      }
    });

    // set an optional global exception handler - all exceptions thrown by service methods end up here
    // if not set each method throws as usual, its up to the developer to catch accordingly
    // if callback are used all exceptions go to the failed method
    client.setServiceExceptionHandler(new ExceptionHandler() {
      @Override
      public void handle(Throwable exception) {
        System.err.println("Error happened:");
        exception.printStackTrace();
        client.disconnect();
      }
    });


    // connect will trigger the authentication
    // AuthException is thrown when failed
    client.connect();

    // cancel pending device auth if necessary
    // client will throw  AuthCanceledException
    client.cancelAuth();

    try {
      // must sleep in this demo to give time to enter  device code
      Thread.sleep(30000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    // Check ins ------------------------------------------------------------------------------------------------

    CheckinService service = client.getService(CheckinService.class);

    // set up callback to get notified when a check in event was processed
    service.onCheckinsChanged(new Callback<List<Checkin>>() {
      @Override
      public void completed(List<Checkin> result) {
        // at this point  all pictures are downloaded
        // access binary content to create a image like:
        InputStream is = result.get(0).getPictureObject().getInputStream();
      }

      @Override
      public void failed(Throwable cause) {
        // error happened, handle appropriately
        // no need to disconnect client here
      }
    });


    // get the event data from web hook
    // i.e. a request is posted to your server with this event data as payload
    String json = "";
    /* Example data:
       {  "object": "event.pushes",
          "id": "12345",
          "created": "2015-02-02T11:40:50+01:00",
          "target": "services.checkins",
          "type": "changed" }
    */

    // process the event
    boolean ok = client.handleEvent(json, false);


    // simple retrieval ------------------------------------------------------------------------------------------------

    final SkeletonService skeletonService = client.getService(SkeletonService.class);

    // get skeleton, without/with callback
    Skeleton skeleton = skeletonService.getSkeleton("skl_60", null);
    System.out.println("got skeleton: " + skeleton);

    skeleton = skeletonService.getSkeleton("skl_60", new Callback<Skeleton>() {
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
    List<Skeleton> skeletons = skeletonService.getSkeletonsAsList(null, new Callback<List<Skeleton>>() {
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

    // get services by class or by id, getting by class is type safe
    TransactionService transactionService = client.getService("smart.transactions");
    IdentService identService = client.getService("smart/idents");

    // select an ident
    List<Ident> availableIdents = identService.getIdents(null);
    if (availableIdents == null) {
      throw new RuntimeException("No idents found.");
    }
    Ident ident = Ident.find("smi_1", availableIdents);
    ident.setValue("pdo28hdal");

    List<Ident> selectedIdents = Arrays.asList(ident);

    List<ProductGroup> groups = Arrays.asList(new ProductGroup("group1", "beverages", 1));

    Basket basket = new Basket();
    basket.addProduct(new Product(1, null, "3378", "5060215249804", "desc1", "5.17", 1999, 7, groups));
    basket.addProduct(new Product(2, null, "34543", "5060215249805", "desc2", "1.5", 999, 19, groups));
    basket.addProduct(new Text("art2", "text1"));
    basket.addProduct(new Text("art2", "text2"));
    basket.addProduct(new Product(3, null, "08070", "60215249807", "desc3", "20", 219, 7, null));
//
    BasketInfo basketInfo = new BasketInfo(1, "EUR");

    Transaction newTrans = new Transaction(basketInfo, basket, selectedIdents);
    newTrans.setMerchantRef("merchant21");
    newTrans.setTransactionRef("transaction99");

    Transaction transaction = transactionService.createTransaction(newTrans, null);

    String type = "demo"; // demo|auto|cash
    // demo instructs the server to simulate a different (random) transaction for each invocation of startTransaction

    Transaction result = transactionService.startTransaction(transaction.getId(), type, null);
    System.out.println("Transaction finished: " + result);

    client.disconnect();

  }

  private static void runThreaded(final ClientConfiguration cfg) {
    for (int i = 0; i < 1; i++) {
      final String id = "device" + i;
      new Thread() {
        @Override
        public void run() {
//          process(id, cfg);
        }
      }.start();
    }
  }
}
