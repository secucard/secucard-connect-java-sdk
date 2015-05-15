package com.secucard.connect.service.general;

import com.secucard.connect.Callback;
import com.secucard.connect.ExceptionHandler;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.QueryParams;
import com.secucard.connect.model.general.Skeleton;
import com.secucard.connect.service.AbstractService;
import com.secucard.connect.service.AbstractServicesTest;
import com.secucard.connect.service.smart.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SkeletonTest extends AbstractServicesTest {
  private SkeletonService service;
  private ObjectList<Skeleton> skeletons;

  @Override
  public void before() throws Exception {
    super.before();
    service = client.getService(SkeletonService.class);
  }

  @Override
  protected void executeTests() throws Exception {
    testFind();
//    testGet();
  }

  private void testFind() throws Exception {

    client.onEvent(new EventListener() {
      @Override
      public void onEvent(Object event) {
        System.out.println("EVENT: " + event);
      }
    });

    client.setServiceExceptionHandler(new ExceptionHandler() {
      @Override
      public void handle(Throwable exception) {
        System.err.println("###" + exception.toString());
        client.disconnect();
      }
    });

//    client.autoDisconnect(10);

    QueryParams queryParams = new QueryParams();
    queryParams.setOffset(1);
    queryParams.setCount(2);
    //queryParams.setFields("a"); seems not to work properly
    queryParams.addSortOrder("a", QueryParams.SORT_ASC);
    queryParams.addSortOrder("b", QueryParams.SORT_DESC);
    queryParams.setQuery("a:abc1? OR (b:*0 AND NOT c:???1??)");
    Callback<ObjectList<Skeleton>> callback = new Callback<ObjectList<Skeleton>>() {
      @Override
      public void completed(ObjectList<Skeleton> result) {
        skeletons = result;
        assertNotNull(skeletons);
        System.out.println(skeletons);
      }

      @Override
      public void failed(Throwable throwable) {
        System.err.print("FAILED: ");
        throwable.printStackTrace();
//        assumeNoException(throwable);
      }
    };
    Thread.sleep(5000);
    Object result = null;
    try {
      result = service.getSkeletons(queryParams, null);
    } catch (Exception e) {
      System.err.print("CATCH: ");
      e.printStackTrace();
    }

    Thread.sleep(5000);
    System.out.println(result);
  }

  private void testGet() throws Exception {
    client.setServiceExceptionHandler(new ExceptionHandler() {
      @Override
      public void handle(Throwable exception) {
        client.disconnect();
        System.err.print("EX:");
        exception.printStackTrace();
      }
    });
    String id = "skl_600";

    Skeleton skeleton = service.getSkeleton(id, null);
    assertNotNull(skeleton);
    assertEquals(id, skeleton.getId());

    skeleton = service.getSkeleton(id, null);

    Thread.sleep(10000);
    System.out.println("done");
  }
}
