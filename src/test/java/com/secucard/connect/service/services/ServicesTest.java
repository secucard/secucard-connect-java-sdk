package com.secucard.connect.service.services;

import com.secucard.connect.Client;
import com.secucard.connect.ClientConfiguration;
import com.secucard.connect.model.services.IdentRequest;
import com.secucard.connect.model.services.IdentResult;
import com.secucard.connect.model.services.idrequest.Person;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
  public void run() throws Exception {
    testGetIdentRequest();
    testGetIdentResult();
//    testIdent();
  }

  private void testGetIdentRequest() throws Exception {
    ServicesService service = client.getService(ServicesService.class);
    client.connect();

    try {
      List<IdentRequest> identRequests = service.getIdentRequests(null, null);
      assertTrue(identRequests.size() > 0);

      String id = identRequests.get(0).getId();
      IdentRequest identRequest = service.getIdentRequest(id, null);
      assertEquals(id, identRequest.getId());
    } finally {
      client.disconnect();
    }
  }

  private void testGetIdentResult() {
    ServicesService service = client.getService(ServicesService.class);
    client.connect();

    try {
      List<IdentResult> identResults = service.getIdentResults(null, null);
      assertTrue(identResults.size() > 0);

      String id = identResults.get(0).getId();
      IdentResult identResult = service.getIdentResult(id, null);
      assertEquals(id, identResult.getId());

    } finally {
      client.disconnect();
    }
  }

  private void testIdent() throws Exception {
    ServicesService service = client.getService(ServicesService.class);
    client.connect();

    try {
      IdentRequest newIr = new IdentRequest();
      newIr.setType(IdentRequest.TYPE_PERSON);
      String transactionId = "TX" + System.currentTimeMillis();
      newIr.setOwnerTransactionId(transactionId);
      Person p = new Person();
      p.setOwnerTransactionId(transactionId);
      p.setFirstname("Hans");
      p.setLastname("Dampf");
      p.setCity("Berlin");
      p.setZipcode("11011");
      p.setGender(Person.GENDER_MALE);
      p.setNationality(Locale.GERMANY);
      p.setCountry(Locale.GERMANY);
      p.setStreet("Platz der Republik 1");
      Date date = new SimpleDateFormat("dd.MM.yyyy").parse("01.01.1951");
      p.setBirthdate(date);
      newIr.addPerson(p);
      newIr = service.createIdentRequest(newIr, null);
      assertEquals("Hans", newIr.getPersons().get(0).getFirstname());

    } finally {
      client.disconnect();
    }
  }
}
