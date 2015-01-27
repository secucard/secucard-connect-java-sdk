package com.secucard.connect.service.services;

import com.secucard.connect.model.services.idresult.Attachment;
import com.secucard.connect.service.AbstractServicesTest;
import com.secucard.connect.service.TestService;
import com.secucard.connect.storage.DataStorage;
import com.secucard.connect.storage.DiskCache;
import junit.framework.Assert;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

public class AttachmentTest extends AbstractServicesTest {

  @Override
  protected void executeTests() throws Exception {
    String url = "http://media2.govtech.com/images/770*1000/dog_flickr.jpg?1&2&3";

    client.getService(TestService.class).setContextToCurrentThread();

    Attachment attachment = new Attachment(url, "jpg");

    DiskCache storage = (DiskCache) context.getDataStorage();

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

    storage.clear();
  }

  @Override
  protected String getConfigString() {
    return "stompEnabled=false\nheartBeatSec=0";
  }
}
