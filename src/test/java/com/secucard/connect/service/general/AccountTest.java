package com.secucard.connect.service.general;

import com.secucard.connect.event.EventListener;
import com.secucard.connect.model.general.accounts.Location.Location;
import com.secucard.connect.service.AbstractServicesTest;
import junit.framework.Assert;

public class AccountTest extends AbstractServicesTest {
   private Object result;

  @Override
  protected void executeTests() throws Exception {

    result = null;
    client.setEventListener(new EventListener() {
      @Override
      public void onEvent(Object event) {
        result = event;
      }
    });

    AccountService service = client.getService(AccountService.class);

    final Location location = new Location(48.138656869103244d, 11.573281288146972d, 1);

    boolean b = service.updateLocation("me", location);

    // event may take a while
    Thread.sleep(30000);

    Assert.assertTrue(b);

    Assert.assertTrue(result != null);

    System.out.println("Got merchants: " + result);
  }

}
