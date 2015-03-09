package com.secucard.connect.service.smart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.secucard.connect.Callback;
import com.secucard.connect.channel.JsonMapper;
import com.secucard.connect.model.general.Notification;
import com.secucard.connect.model.smart.*;
import com.secucard.connect.service.AbstractServicesTest;
import org.junit.Assert;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeNoException;

public class SmartTest extends AbstractServicesTest {
  private Device device;
  private Ident ident;

  @Override
  protected void executeTests() throws Exception {
//    testIdents();
//    testDevice();
    testTransaction();
//    testCheckins();

//    Thread.sleep(3 * 60 * 1000);
//    System.out.println("done");
  }

  public void testCheckins() throws Exception {
    final String json = "{\n" +
        "    \"object\": \"event.pushes\",\n" +
        "    \"id\": \"XXX_XXXXXXXXXXX\",\n" +
        "    \"created\": \"2015-02-02T11:40:50+01:00\",\n" +
        "    \"target\": \"smart.checkins\",\n" +
        "    \"type\": \"changed\"}";
    CheckinService service = client.getService(CheckinService.class);

    service.onCheckinsChanged(new Callback<List<Checkin>>() {
      @Override
      public void completed(List<Checkin> checkins) {
        try {
          for (Checkin checkin : checkins) {
            byte[] contents = checkin.getPictureObject().getContents();
            Assert.assertTrue(contents.length > 0);
          }
        } catch (IOException e) {
          assumeNoException(e);
        }
      }

      @Override
      public void failed(Throwable cause) {
        assumeNoException(cause);
      }
    });

//    boolean b = client.handleEvent(json);
//    Assert.assertTrue(b);

    Thread.sleep(5 * 60 * 1000);
    System.out.println();
  }

  private void testIdents() {
    IdentService service = client.getService("smart/idents");

    List<Ident> idents = service.getIdents(null);
    assertTrue(idents.size() > 0);

    ident = Ident.find(idents.get(0).getId(), idents);
    assertNotNull(ident);
  }

  private void testDevice() {
    DeviceService service = client.getService(DeviceService.class);

    device = new Device("device1");
  }

  private void testTransaction() throws IOException {
    TransactionService service = client.getService("smart.transactions");
    service.onCashierDisplayChanged(new Callback<Notification>() {
      @Override
      public void completed(Notification result) {
        System.out.println(result);
      }

      @Override
      public void failed(Throwable cause) {

      }
    });

    Basket basket = new Basket();
    basket.addProduct(new Product(1, null, "3378", "5060215249804", "desc1", "5.0", 1999, 19, null));
    basket.addProduct(new Product(2, null,  "34543", "5060215249805", "desc2", "1.5", 999, 2, null));
    basket.addProduct(new Text("art2", "text1"));
    basket.addProduct(new Text("art2", "text2"));
    basket.addProduct(new Product(3, null, "08070", "60215249807", "desc3", "20", 219, 2, null));

    BasketInfo basketInfo = new BasketInfo(13650, "EUR");

    Transaction newTrans = new Transaction(basketInfo, basket, Arrays.asList(ident));

    newTrans = JsonMapper.get().map(getClass().getClassLoader().getResource("transaction.json"), Transaction.class);

    Transaction transaction = service.createTransaction(newTrans, null);
    assertNotNull(transaction);

    transaction = service.updateTransaction(transaction, null);

    Transaction result = service.startTransaction(transaction.getId(), "demo", null);
    assertNotNull(result);
  }
}
