package com.secucard.connect.service.services;

import com.secucard.connect.Callback;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.model.general.Event;
import com.secucard.connect.model.services.IdentRequest;
import com.secucard.connect.model.services.IdentResult;
import com.secucard.connect.model.services.idrequest.Person;
import com.secucard.connect.service.AbstractServicesTest;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ServicesTest extends AbstractServicesTest {

  @Test
  public void test() throws Exception {
    client.setEventListener(new EventListener() {
      @Override
      public void onEvent(Object event) {
        System.out.println(event);
      }
    });
//    testGetIdentRequest();
//    testGetIdentResult();
//    testIdent();
    testEvents();
  }

  public void testEvents() throws Exception {

    final String json = "{\n" +
        "    \"object\": \"event.pushes\",\n" +
        "    \"id\": \"XXX_XXXXXXXXXXX\",\n" +
        "    \"created\": \"2015-02-02T11:40:50+01:00\",\n" +
        "    \"target\": \"services.identrequests\",\n" +
        "    \"type\": \"changed\",\n" +
        "    \"data\": [\n" +
        "            {\n" +
        "                \"object\": \"services.identrequests\",\n" +
        "                \"id\": \"XXX_XXXXXXXXXXXXXXXXXXXXXXXX\"\n" +
        "            }\n" +
        "    ]\n" +
        "}";

    IdentService service = client.getService(IdentService.class);


    service.registerEventHandler(new IdentService.IdentEventHandler() {
      @Override
      public boolean downloadAttachments(List<IdentRequest> requests) {
        return false;
      }
    });

    client.connect();

    try {
      Object o = client.handleEvent(json, null);
      System.out.println();
    } finally {
      client.disconnect();
    }
  }

  private void testGetIdentRequest() throws Exception {
    IdentService service = client.getService(IdentService.class);
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

  private void testGetIdentResult() throws Exception {
    IdentService service = client.getService(IdentService.class);

    service.getServiceEventListener().onEvent("Event");

    try {
      client.connect();
      List<IdentResult> identResults = service.getIdentResults(null, null, true);
      assertTrue(identResults.size() > 0);

      String id = identResults.get(0).getId();
      IdentResult identResult = service.getIdentResult(id, null, true);
      assertEquals(id, identResult.getId());

    } finally {
      client.disconnect();
    }
  }

  private void testIdent() throws Exception {
    IdentService service = client.getService(IdentService.class);
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
