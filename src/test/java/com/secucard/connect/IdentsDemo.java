package com.secucard.connect;

import com.secucard.connect.model.services.IdentRequest;
import com.secucard.connect.model.services.idrequest.Person;
import com.secucard.connect.service.services.IdentService;

import java.text.SimpleDateFormat;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class IdentsDemo {
  public static void main(String[] args) throws Exception {

    // todo: explain necessary settings in config

    final ClientConfiguration cfg = ClientConfiguration.fromProperties("identdemo-config.properties");
    final Client client = Client.create("identdemo", cfg);

    IdentService service = client.getService(IdentService.class);
    // or alternatively
    // IdentService service = client.getService("services.idents");
    // IdentService service = client.getService("services/idents");

    client.connect();

    try {
      IdentRequest identRequest = new IdentRequest();
      identRequest.setType(IdentRequest.TYPE_PERSON);
      String transactionId = "TX" + System.currentTimeMillis();
      identRequest.setOwnerTransactionId(transactionId);
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
      p.setBirthdate(new SimpleDateFormat("dd.MM.yyyy").parse("01.01.1951"));
      identRequest.addPerson(p);
      identRequest = service.createIdentRequest(identRequest, null);
      assertEquals("Hans", identRequest.getPersons().get(0).getFirstname());

      // todo: complete flow


    } finally {
      // important to close the client properly at last, avoids leaking resources
      client.disconnect();
    }
  }
}
