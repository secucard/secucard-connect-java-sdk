package com.secucard.connect.service.general;

import com.secucard.connect.Callback;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.event.Events;
import com.secucard.connect.model.QueryParams;
import com.secucard.connect.model.general.Skeleton;
import com.secucard.connect.service.AbstractServicesTest;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeNoException;

public class SkeletonTest extends AbstractServicesTest {
  private SkeletonService service;
  private List<Skeleton> skeletons;

  @Override
  public void before() throws Exception {
    super.before();
    service = client.getService(SkeletonService.class);
    client.setEventListener(Events.ConnectionStateChanged.class, new EventListener<Events.ConnectionStateChanged>() {
      @Override
      public void onEvent(Events.ConnectionStateChanged event) {
        System.out.println("Client is connected: " + event.connected);
      }
    });
  }

  @Override
  protected void executeTests() throws Exception {
//    testFind();
    testGet();
  }

  private void testFind() throws Exception {
    QueryParams queryParams = new QueryParams();
    queryParams.setOffset(1);
    queryParams.setCount(2);
    //queryParams.setFields("a"); seems not to work properly
    queryParams.addSortOrder("a", QueryParams.SORT_ASC);
    queryParams.addSortOrder("b", QueryParams.SORT_DESC);
    queryParams.setQuery("a:abc1? OR (b:*0 AND NOT c:???1??)");
    service.getSkeletons(null, new Callback<List<Skeleton>>() {
      @Override
      public void completed(List<Skeleton> result) {
        skeletons = result;
        assertNotNull(skeletons);
      }

      @Override
      public void failed(Throwable throwable) {
        assumeNoException(throwable);
      }
    });

    Thread.sleep(2000);
  }

  private void testGet() throws Exception {
    String id = "skl_60";

    Skeleton skeleton = service.getSkeleton(id, null);
    assertNotNull(skeleton);
    assertEquals(id, skeleton.getId());

    skeleton = service.getSkeleton(id, null);

    Thread.sleep(10000);
    System.out.println("done");
  }
}
