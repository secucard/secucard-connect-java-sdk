package com.secucard.connect.auth;

import com.secucard.connect.service.AbstractServicesTest;

public class ClientAuthTest extends AbstractServicesTest {

  @Override
  public void test() throws Exception {
    try {
      AuthTestService service = client.getService(AuthTestService.class);
      service.clientIdAuth();
    } finally {
      client.disconnect();
    }
  }
}
