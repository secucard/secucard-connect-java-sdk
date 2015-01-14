package com.secucard.connect;

import com.secucard.connect.event.EventListener;
import com.secucard.connect.model.services.IdentRequest;
import com.secucard.connect.model.services.IdentResult;
import com.secucard.connect.model.services.idrequest.Person;
import com.secucard.connect.service.services.IdentService;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class IdentsDemo {
  public static void main(String[] args) throws Exception {

    final ClientConfiguration cfg = ClientConfiguration.fromProperties("config-identdemo.properties");
    final Client client = Client.create("identdemo", cfg);

    // setting any instance as a receiver for events the client produces, optional for this use case
    client.setEventListener(new EventListener() {
      @Override
      public void onEvent(Object event) {
        System.out.println("Got event: " + event);
      }
    });

    IdentService service = client.getService(IdentService.class);
    // or alternatively
    // IdentService service = client.getService("services.idents");
    // IdentService service = client.getService("services/idents");

    client.connect();

    try {

      // creating a request,
      // if successfully the new ident request is returned otherwise a exception is thrown

      IdentRequest request = new IdentRequest();
      String transactionId = "TX" + System.currentTimeMillis();

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

      request.setOwnerTransactionId(transactionId);
      request.setType(IdentRequest.TYPE_PERSON);
      request.addPerson(p);

      IdentRequest newRequest = service.createIdentRequest(request, null);
      String id = newRequest.getId();


      // retrieving a single request

      request = service.getIdentRequest(id, null);



      // retrieving the results of a specific ident request
      // returns null if nothing available, throws an exception if a error occurs

      IdentResult result = service.getIdentResultByRequestId(id, null);



      // or getting all results (and selecting manually)
      // no query param is used so no filtering applies to the result

      List<IdentResult> results = service.getIdentResults(null, null);
      // iterate over results ...


    } finally {
      // important to close the client properly at last, avoids leaking resources
      client.disconnect();
    }
  }
}
