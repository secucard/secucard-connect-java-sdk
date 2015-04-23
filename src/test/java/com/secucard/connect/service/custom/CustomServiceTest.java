package com.secucard.connect.service.custom;

import com.secucard.connect.auth.AppUserCredentials;
import com.secucard.connect.service.AbstractServicesTest;
import junit.framework.Assert;

public class CustomServiceTest extends AbstractServicesTest {

  @Override
  public void before() throws Exception {
    super.before();
//    context.getAuthProvider().setCredentials(new AppUserCredentials());"checkout@secucard.com", "checkout");
  }

  @Override
  protected void executeTests() throws Exception {
    AppService service = client.getService(AppService.class);
    Object result = service.getFoo("123");
    Assert.assertNotNull(result);

    service.updateLocation();

    //    Object list = service.getList();

//    Map res1 = service.invoke("app_a1621caf12f1499c7ffab0c4", "getFoo", "123", Map.class, null);
//    String res2 = service.invoke("app_a1621caf12f1499c7ffab0c4", "getDummytimeout", "123", String.class, null);
//    String res2 = service.invoke("app_c3621a0f14f1491b1ffab1a0", "getFoo", "123", String.class, null);
//    String res3 = service.invoke("app_c3621a0f14f1491b1ffab1a0", "getBar", "123", String.class, null);

//    String foo = service.getFoo("123");
  }
}
