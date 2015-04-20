package com.secucard.connect.auth;

import com.secucard.connect.service.AbstractServicesTest;

public class ClientAuthTest extends AbstractServicesTest {

  @Override
  public void test() throws Exception {

    // do no connect, this is done
    try {
      AuthTestService service = client.getService(AuthTestService.class);
      service.clientIdAuth();
    } finally {
      client.disconnect();
    }
  }
}
