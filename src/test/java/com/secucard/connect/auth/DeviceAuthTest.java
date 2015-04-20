package com.secucard.connect.auth;

import com.secucard.connect.service.AbstractServicesTest;

public class DeviceAuthTest extends AbstractServicesTest {

  @Override
  public void test() throws Exception {

    // do no connect, this is done
    try {
      AuthTestService service = client.getService(AuthTestService.class);
      service.deviceAuth();
//      service.deviceAuthStepped();
    } finally {
      client.disconnect();
    }
  }

}
