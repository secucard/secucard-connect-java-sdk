package com.secucard.connect.service;

import com.secucard.connect.Client;
import com.secucard.connect.ClientConfiguration;
import com.secucard.connect.ClientContext;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;


public class AbstractServicesTest {
  protected Client client;
  protected ClientConfiguration clientConfiguration;
  protected ClientContext context;

  @Before
  public void before() throws Exception {
    initLogging();

    // todo: enable changing default channel in config to test other channels
    String  cfg = getConfigString();
    if (cfg == null) {
      clientConfiguration = ClientConfiguration.fromProperties(getConfigPath());
    } else {
      clientConfiguration = ClientConfiguration.fromStream(new ByteArrayInputStream(cfg.getBytes()));
    }


    client = Client.create("test", clientConfiguration);

    context = client.getService(TestService.class).getContext();
  }

  public static void initLogging() {
    String path = AbstractServicesTest.class.getClassLoader().getResource("logging.properties").getPath();
    System.setProperty("java.util.logging.config.file", path);
  }

  @Test
  public void test() throws Exception {
    client.connect();
    try {
      executeTests();
    } finally {
      client.disconnect();
    }
  }

  protected void executeTests() throws Exception {
  }

  protected String getConfigString() {
    return null;
  }

  protected String getConfigPath() {
    return "config.properties";
  }
}
