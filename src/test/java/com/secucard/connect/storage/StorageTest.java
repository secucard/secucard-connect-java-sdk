package com.secucard.connect.storage;

import junit.framework.Assert;
import org.junit.Test;

import java.io.IOException;

public class StorageTest {

  @Test
  public void diskStorage() throws Exception {

    DiskCache storage = new DiskCache("/tmp/teststore/test1");

    storage.save("1", "1");
    storage.save("12", "12");
    storage.save("2", "2");
    storage.save("3", "3");

    Assert.assertTrue(storage.size() == 4);

    Assert.assertTrue(storage.get("1").equals("1"));
    Assert.assertTrue(storage.get("12").equals("12"));

    storage.clear("1", null);
    Assert.assertNull(storage.get("1"));

    storage.clear("1*", null);
    Assert.assertNull(storage.get("12"));
    Assert.assertNotNull(storage.get("2"));

    storage.clear();
    Assert.assertTrue(storage.size() == 0);

    storage.destroy();
    Assert.assertTrue(storage.size() == 0);
  }
}
