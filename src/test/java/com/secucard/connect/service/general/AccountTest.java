package com.secucard.connect.service.general;

import com.secucard.connect.event.EventListener;
import com.secucard.connect.model.general.Location;
import com.secucard.connect.model.general.MerchantList;
import com.secucard.connect.service.AbstractServicesTest;
import junit.framework.Assert;

public class AccountTest extends AbstractServicesTest {
   private Object result;

  @Override
  protected void executeTests() throws Exception {

    result = null;
    client.onEvent(new EventListener() {
      @Override
      public void onEvent(Object event) {
        System.out.println(event);
      }
    });

    AccountService service = client.getService(AccountService.class);

    service.onMerchantsChanged(new EventListener<MerchantList>() {
      @Override
      public void onEvent(MerchantList event) {
        result = event;
      }
    });

    final Location location = new Location(48.138656869103244d, 11.573281288146972d, 1);

    boolean b = service.updateLocation("me", location);

    // event may take a while
    Thread.sleep(30000);

    Assert.assertTrue(b);

    Assert.assertTrue(result != null);

    System.out.println("Got merchants: " + result);
  }

}
