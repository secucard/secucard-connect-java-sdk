package com.secucard.connect.storage;

import junit.framework.Assert;
import org.junit.Test;

import java.io.IOException;

public class StorageTest {


  @Test
  public void diskStorage() throws IOException {

    SimpleFileDataStorage storage = new SimpleFileDataStorage("teststore");

    storage.save("1", "21");
    Object o = storage.get("1");
    Assert.assertTrue(o.equals("21"));
    storage.remove();
  }
}
