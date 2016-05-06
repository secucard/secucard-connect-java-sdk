package com.secucard.connect.product.common;

import com.secucard.connect.SecucardConnect;
import com.secucard.connect.client.DataStorage;
import com.secucard.connect.product.common.model.ObjectList;
import com.secucard.connect.product.common.model.QueryParams;
import com.secucard.connect.product.payment.model.Customer;
import com.secucard.connect.testsupport.TestAuthDetails;
import com.secucard.connect.testsupport.TestCreds;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public class ListScrollTest {

  @Test
  public void test() {
    SecucardConnect.Configuration cfg = SecucardConnect.Configuration.get();

    cfg.clientAuthDetails = new TestAuthDetails(TestCreds.CLI_ID_PAY, TestCreds.CLI_SEC_PAY);

    SecucardConnect sc = SecucardConnect.create(cfg);

    try {
      sc.open();
      QueryParams qp = new QueryParams();
      qp.setCount(2);

      ObjectList<Customer> list = sc.payment.customers.getScrollableList(qp, "10s");
      assertTrue(list.getCount() == 2);
      assertTrue(list.getTotalCount() > 2);

      list = sc.payment.customers.getNextBatch(list.getScrollId());
      assertTrue(list.getCount() == 2);
      assertTrue(list.getTotalCount() == 0);

      // expiring
      list = sc.payment.customers.getScrollableList(qp, "1s");
      Thread.sleep(10000); // todo: not sure how long to wait, sometime 10s are enough, sometimes not, need figure out
      try {
        sc.payment.customers.getNextBatch(list.getScrollId());
        fail("Expected exception because of expired search context");
      } catch (Exception e) {
        // ignore
      }

    } catch (Exception e) {
      e.printStackTrace();
      fail();
    } finally {
      sc.close();
      try {
        cfg.dataStorage.destroy();
      } catch (DataStorage.DataStorageException e) {
        e.printStackTrace();
        fail();
      }
    }
  }
}