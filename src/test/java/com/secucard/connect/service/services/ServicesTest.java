package com.secucard.connect.service.services;

import com.secucard.connect.event.EventListener;
import com.secucard.connect.model.general.Address;
import com.secucard.connect.model.general.Contact;
import com.secucard.connect.model.services.IdentRequest;
import com.secucard.connect.model.services.IdentResult;
import com.secucard.connect.model.services.idrequest.Person;
import com.secucard.connect.service.AbstractServicesTest;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ServicesTest extends AbstractServicesTest {

  @Test
  public void test() throws Exception {
    client.onEvent(new EventListener() {
      @Override
      public void onEvent(Object event) {
        System.out.println(event);
      }
    });
//    testGetIdentRequest();
    testGetIdentResult();
//    testIdent();
//    testEvents();
  }

  @Override
  protected String getConfigString() {
    return "auth.clientId=69bfc7183507823569d06ad81a40073a\n" +
        "auth.clientSecret=ee5fb67afedda1c4eb863c73a940ec1fcea0d5b5a92f4cf1697c3d4f34b2fd8a";
  }

  public void testEvents() throws Exception {

    final String json1 = "{\n" +
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

    final String json = "{\"object\":\"event.pushes\",\"id\":\"evt_BU2Q47PHDBTHE3WQ84KJ78AWG284EN\",\"created\":\"2015-04-01T14:30:27+02:00\",\"target\":\"services.identrequests\",\"type\":\"changed\",\"data\":[{\"object\":\"services.identrequests\",\"id\":\"SIR_WDVT8TR5P2Y8QMAZB5GQGMWHNZXWA2\"}]}";

    final List<String> list = new ArrayList<>();
    IdentService service = client.getService(IdentService.class);

    service.onIdentRequestChanged(new IdentService.IdentEventHandler() {
      @Override
      public boolean downloadAttachments(List<IdentRequest> requests) {
        return true;
      }

      @Override
      public void completed(List<IdentResult> result) {
        list.add("");
        System.err.println("### " + list.size() + " ### " + result);
//        Assert.assertTrue(result.size() > 0);
      }

      @Override
      public void failed(Throwable cause) {
        cause.printStackTrace();
//        assumeNoException(cause);
      }
    });

    client.connect();

    try {
      for (int i = 0; i < 1; i++) {
        client.handleEvent(json, false);
        Thread.sleep(100);
      }
      Thread.sleep(10000);
      System.out.println(list.size());
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


    try {
      client.connect();
      List<IdentResult> identResults = service.getIdentResults(null, null, true);
      assertTrue(identResults.size() > 0);  // SIR_2HBMSHU6N2Y9BRHFR5GQG0TSX8ZWAN

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
      Contact contact = new Contact();
      Address address = new Address();
      address.setCity("city");
      address.setStreet("street");
      address.setStreetNumber("number");
      address.setCountry(Locale.GERMANY);
      address.setPostalCode("88888");
      contact.setAddress(address);
      contact.setCompanyName("companyname");
      contact.setDateOfBirth(new SimpleDateFormat("dd.MM.yyyy").parse("1.4.1888"));
      contact.setEmail("email");
      contact.setForename("forename");
      contact.setSurname("surname");
      contact.setPhone("110");
      contact.setGender(Contact.GENDER_FEMALE);
      contact.setName("name");
      contact.setBirthPlace("birthplace");
      contact.setMobile("12345");
      contact.setNationality(Locale.GERMANY);
      contact.setSalutation("salutation");
      contact.setTitle("title");
      contact.setUrlWebsite("url");
      p.setContact(contact);
      newIr.addPerson(p);
      newIr.demo = "1";
      newIr = service.createIdentRequest(newIr, null);
      assertEquals("forename", newIr.getPersons().get(0).getContact().getForename());

    } finally {
      client.disconnect();
    }
  }
}
