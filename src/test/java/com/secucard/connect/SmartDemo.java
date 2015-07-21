/*
 * Copyright (c) 2015. hp.weber GmbH & Co secucard KG (www.secucard.com)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.secucard.connect;

import com.secucard.connect.auth.CredentialsProvider;
import com.secucard.connect.auth.DefaultTokenStore;
import com.secucard.connect.auth.exception.AuthDeniedException;
import com.secucard.connect.auth.model.ClientCredentials;
import com.secucard.connect.auth.model.DeviceAuthCode;
import com.secucard.connect.auth.model.DeviceCredentials;
import com.secucard.connect.auth.model.OAuthCredentials;
import com.secucard.connect.client.Callback;
import com.secucard.connect.client.SecucardConnectException;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.event.Events;
import com.secucard.connect.product.common.model.QueryParams;
import com.secucard.connect.product.general.model.Notification;
import com.secucard.connect.product.smart.CheckinService;
import com.secucard.connect.product.smart.IdentService;
import com.secucard.connect.product.smart.Smart;
import com.secucard.connect.product.smart.TransactionService;
import com.secucard.connect.product.smart.model.*;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class SmartDemo {

  public static void main(String[] args) throws Exception {

    final SecucardConnect.Configuration cfg = SecucardConnect.Configuration.get();

    cfg.id = "smartdemo";

    cfg.credentialsProvider = new CredentialsProvider() {
      @Override
      public OAuthCredentials getCredentials() {
        return new DeviceCredentials("id", "secret", "device");
      }

      @Override
      public ClientCredentials getClientCredentials() {
        return (ClientCredentials) getCredentials();
      }
    };
    cfg.tokenStore = new DefaultTokenStore("smartdemo-ts");

    final SecucardConnect client = SecucardConnect.create(cfg);

    // set up event listener, especially required to handle auth events!
    client.onAuthEvent(new EventListener() {
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


    // cancel pending device auth if necessary
    // client will throw  AuthCanceledException
//    client.cancelAuth();

    // must sleep in this demo to give time to enter device code
    // Thread.sleep(30000);

    // Checkins ------------------------------------------------------------------------------------------------

    CheckinService service = client.service(Smart.Checkins);

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
//    boolean ok = client.handleEvent(json, false);


    // Smart Transaction  ----------------------------------------------------------------------------------------

    TransactionService transactionService = client.service(Smart.Transactions);
    IdentService identService = client.service(Smart.Idents);

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

      // select an ident
      List<Ident> availableIdents = identService.getSimpleList(new QueryParams());
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

      Transaction transaction = transactionService.create(newTrans, null);
      assert (transaction.getStatus().equals(Transaction.STATUS_CREATED));


      // you may edit some transaction data and update
      newTrans.setMerchantRef("merchant");
      transaction.setTransactionRef("trans1");
      transaction = transactionService.update(transaction, null);

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
      e.printStackTrace();

      if (e instanceof SecucardConnectException) {
        SecucardConnectException ex = (SecucardConnectException) e;
        if (ex.getUserMessage() != null && !SecucardConnectException.INTERNAL.equals(ex.getCode())) {
          System.err.println(ex.getUserMessage());
        } else {
          System.err.println("### Unexpected error happened, please contact ... for assistance and provide this id: "
              + ex.getSupportId());
        }
      } else {
        System.err.println("### Unexpected error happened, please contact ... for assistance.");
      }

    } finally {
      client.close();
    }
  }
}
