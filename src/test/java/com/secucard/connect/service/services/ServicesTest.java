package com.secucard.connect.service.services;

import com.secucard.connect.Client;
import com.secucard.connect.ClientConfiguration;
import com.secucard.connect.model.services.IdentRequest;
import com.secucard.connect.model.services.Person;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ServicesTest {
  private Client client;

  @Before
  public void before() throws Exception {
    ClientConfiguration cfg = ClientConfiguration.fromProperties("config.properties");
    client = Client.create("test", cfg);
  }

  @Test
  public void testIdentRequest() throws Exception {
    ServicesService service = client.getService(ServicesService.class);

    try {
      client.connect();

      List<IdentRequest> identRequests = service.getIdentRequests(null, null);
      assertTrue(identRequests.size() > 0);

      String id = identRequests.get(0).getId();
      IdentRequest identRequest = service.getIdentRequest(id, null);
      assertEquals(id, identRequest.getId());

      identRequest.getPersons().get(0).setFirstname("Kalle");
      identRequest.setId(null);

      IdentRequest newIr = new IdentRequest();
      newIr.setType(IdentRequest.TYPE_PERSON);
      newIr.setOwnerTransactionId("123456789");
      Person p = new Person();
      p.setFirstname("Kalle");
      newIr.addPerson(p);
      newIr = service.createIdentRequest(newIr, null);
      assertEquals("Kalle", newIr.getPersons().get(0).getFirstname());

    } finally {
      client.disconnect();
    }
  }
}
