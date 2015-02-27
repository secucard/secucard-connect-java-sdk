package com.secucard.connect.service.smart;

import com.secucard.connect.Callback;
import com.secucard.connect.model.smart.*;
import com.secucard.connect.service.AbstractServicesTest;
import org.junit.Assert;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
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
//    testTransaction();
    testCheckins();
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
            byte[] contents = checkin.getPicture().getContents();
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
    boolean b = client.handleEvent(json);

    Assert.assertTrue(b);

    Thread.sleep(10000);
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
    boolean ok = service.registerDevice(device, null);
    assertTrue(ok);
  }

  private void testTransaction() {
    TransactionService service = client.getService("smart.transactions");

    Basket basket = new Basket();
    basket.addProduct(new Product("art1", "3378", "5060215249804", "desc1", "5.0", "19.99", "19", "EUR", null));
    basket.addProduct(new Product("art2", "34543", "5060215249805", "desc2", "1.5", "9.99", "2", "EUR", null));
    basket.addProduct(new Text("art2", "text1"));
    basket.addProduct(new Text("art2", "text2"));
    basket.addProduct(new Product("art2", "08070", "60215249807", "desc3", "20", "2.19", "50", "EUR", null));

    BasketInfo basketInfo = new BasketInfo("136.50", "EUR");

    Transaction newTrans = new Transaction(device.getId(), basketInfo, basket, Arrays.asList(ident));

    Transaction transaction = service.createTransaction(newTrans, null);
    assertNotNull(transaction);

    TransactionResult result = service.startTransaction(transaction.getId(), "demo", null);
    assertNotNull(result);
  }
}
