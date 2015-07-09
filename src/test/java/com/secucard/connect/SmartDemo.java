package com.secucard.connect;

import com.secucard.connect.auth.AuthCanceledException;
import com.secucard.connect.auth.AuthException;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.event.Events;
import com.secucard.connect.model.QueryParams;
import com.secucard.connect.model.auth.DeviceAuthCode;
import com.secucard.connect.model.general.Notification;
import com.secucard.connect.model.smart.*;
import com.secucard.connect.service.smart.CheckinService;
import com.secucard.connect.service.smart.IdentService;
import com.secucard.connect.service.smart.TransactionService;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class SmartDemo {

  public static void main(String[] args) throws Exception {

    final ClientConfiguration cfg = ClientConfiguration.fromProperties("config-smartdemo.properties");
    // or use default: ClientConfiguration.getDefault();

    final Client client = Client.create("device1", cfg);
    // Android usage (passing android.content.Context): Client.create(id, cfg, getApplication());
    // must additionally set androidMode=true in configuration file

    // set up event listener, especially required to handle auth events!
    client.onEvent(new EventListener() {
      @Override
      public void onEvent(Object event) {
        System.out.println("### Got event: " + event);

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

    // override above
    client.onConnectionStateChanged(new EventListener<Events.ConnectionStateChanged>() {
      @Override
      public void onEvent(Events.ConnectionStateChanged event) {
        System.out.println(event.connected ? "### ON" : "### OFF");
      }
    });

    // set an optional global exception handler - all exceptions thrown by service methods end up here
    // if not set each method throws as usual, its up to the developer to catch accordingly
    // if callback are used all exceptions go to the failed method
   /* client.setServiceExceptionHandler(new ExceptionHandler() {
      @Override
      public void handle(Throwable exception) {
      }
    });*/


    // connect will trigger the authentication
    // AuthException is thrown when failed
    try {
      client.connect();
    } catch (AuthCanceledException e) {
      System.err.println("### Auth canceled: " + e);
      return;
    } catch (AuthException e) {
      System.err.println("### Auth failed: " + e);
      return;

    } catch (Exception e) {
      // other reason
      e.printStackTrace();
      return;
    }


    // cancel pending device auth if necessary
    // client will throw  AuthCanceledException
//    client.cancelAuth();

    // must sleep in this demo to give time to enter device code
    // Thread.sleep(30000);

    // Checkins ------------------------------------------------------------------------------------------------

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


    // get the event data from web hook, i.e. a request is posted to your server with this event data as payload
    String json = "";
    /* Example data:
       {  "object": "event.pushes",
          "id": "12345",
          "created": "2015-02-02T11:40:50+01:00",
          "target": "services.checkins",
          "type": "changed" }
    */

    // process the event
    // boolean ok = client.handleEvent(json, false);


    // Smart Transaction  ----------------------------------------------------------------------------------------

    TransactionService transactionService = client.getService(TransactionService.class);
    IdentService identService = client.getService(IdentService.class);

    transactionService.onCashierDisplayChanged(new Callback<Notification>() {
      @Override
      public void completed(Notification result) {
        System.out.println("### Cashier Notification: " + result.getText());
      }

      @Override
      public void failed(Throwable cause) {
        cause.printStackTrace();
      }
    });

    try {

      if (true) {

        while (true) {
          QueryParams queryParams = new QueryParams();
          queryParams.setCount(6);
//          List<Transaction> list = transactionService.getList(queryParams, null);
          Thread.sleep(60000);
        }
      }

      // select an ident
      List<Ident> availableIdents = identService.getIdents(null);
      if (availableIdents == null) {
        throw new RuntimeException("No idents found.");
      }

      Ident ident = Ident.find("smi_1", availableIdents);
      ident.setValue("pdo28hdal");

      Basket basket = new Basket();
      basket.addProduct(
          new Product(1, null, "3378", "5060215249804", "desc1", "1", 1, 20, Arrays.asList(new ProductGroup("group1", "beverages", 1)))
      );
      BasketInfo basketInfo = new BasketInfo(1, "EUR");

      Transaction newTrans = new Transaction(basketInfo, basket, Arrays.asList(ident));

      Transaction transaction = transactionService.createTransaction(newTrans, null);
      assert (transaction.getStatus().equals(Transaction.STATUS_CREATED));


      // you may edit some transaction data and update
      newTrans.setMerchantRef("merchant");
      transaction.setTransactionRef("trans1");
      transaction = transactionService.updateTransaction(transaction, null);

      // demo|auto|cash, demo instructs the server to simulate a different (random) transaction for each invocation of
      // startTransaction, also different formatted receipt lines will be returned
      String type = "demo";

      transaction = transactionService.startTransaction(transaction.getId(), type, null);
      assert (transaction.getStatus().equals(Transaction.STATUS_OK));

      System.out.println("### Transaction started: " + transaction);

      // cancel the trans
      boolean ok = transactionService.cancel(transaction.getId(), null);

      //  status has now changed
      transaction = transactionService.get(transaction.getId(), null);
      assert (transaction.getStatus().equals(Transaction.STATUS_CANCELED));

    } catch (Exception e) {
      if (e instanceof ServerErrorException) {
        ServerErrorException err = (ServerErrorException) e;
        if (err.getUserMessage() != null) {
          // display to the user, so he may fix
          System.err.println("### " + err.getUserMessage());
        } else if (err.getSupportId() != null) {
          System.err.println("### Unexpected error happened, please contact ... for assistance and provide this id: "
              + err.getSupportId());
        } else {
          System.err.println("### Unexpected error happened, please contact ... for assistance.");
        }
      } else {
        System.err.println("### Unexpected error happened, please contact ... for assistance.");
      }
      e.printStackTrace();

    } finally {
      client.disconnect();
    }
  }
}
