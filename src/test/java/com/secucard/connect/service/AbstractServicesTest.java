package com.secucard.connect.service;

import com.secucard.connect.Client;
import com.secucard.connect.ClientConfiguration;
import org.junit.Before;
import org.junit.Test;


public class AbstractServicesTest {
  protected Client client;
  protected ClientConfiguration clientConfiguration;

  @Before
  public void before() throws Exception {
    initLogging();

    // todo: enable changing default channel programmatically in config to test other channels
    clientConfiguration = ClientConfiguration.fromProperties("config.properties");

    client = Client.create("test", clientConfiguration);
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
}
