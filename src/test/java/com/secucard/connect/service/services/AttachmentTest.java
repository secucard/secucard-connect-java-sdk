package com.secucard.connect.service.services;

import com.secucard.connect.model.services.idresult.Attachment;
import com.secucard.connect.service.AbstractServicesTest;
import com.secucard.connect.storage.SimpleFileDataStorage;
import junit.framework.Assert;

import java.io.FileOutputStream;
import java.io.InputStream;

public class AttachmentTest extends AbstractServicesTest {

  @Override
  public void test() throws Exception {
    Attachment attachment = new Attachment("http://media2.govtech.com/images/770*1000/dog_flickr.jpg", "jpg");

    SimpleFileDataStorage storage = new SimpleFileDataStorage("teststore");

    attachment.setStorage(storage);

    attachment.download();

    InputStream in = attachment.getInputStream();
    in.close();

    byte[] contents = attachment.getContents();

    FileOutputStream out = new FileOutputStream("att-test");
    out.write(contents);

    storage.clear(System.currentTimeMillis() - 1000 * 60 * 60 * 2);
    Assert.assertTrue(storage.size() == 1);

    storage.clear(System.currentTimeMillis());
    Assert.assertTrue(storage.size() == 0);

    storage.remove();
  }
}
