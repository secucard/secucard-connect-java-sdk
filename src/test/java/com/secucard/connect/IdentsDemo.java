package com.secucard.connect;

import com.secucard.connect.event.EventListener;
import com.secucard.connect.model.services.IdentRequest;
import com.secucard.connect.model.services.IdentResult;
import com.secucard.connect.model.services.idrequest.Person;
import com.secucard.connect.service.services.IdentService;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.Assume.assumeNoException;

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


      // 1. retrieving the results of an ident request by manual polling:
      // returns null if nothing available yet, throws an exception if a error occurs, result else
      List<IdentResult> results = service.getIdentResultsByRequestIds(Arrays.asList(id), null, false);


      // or 2. by responding on web hook event: 
      // provide a special handler for the event
      // when event happens call Client.handle() and pass event JSON data, result will be the ident result list

      service.onIdentRequestChanged(new IdentService.IdentEventHandler() {
        @Override
        public boolean downloadAttachments(List<IdentRequest> requests) {
          return false;
        }

        @Override
        public void completed(List<IdentResult> result) {
          // handle ident result data  ...
        }

        @Override
        public void failed(Throwable cause) {
          // handle fail ...
        }
      });

      String jsonEventData = "..."; /* get from web server, example:
       {  "object": "event.pushes",
          "id": "XXX_XXXXXXXXXXX",
          "created": "2015-02-02T11:40:50+01:00",
          "target": "services.identrequests",
          "type": "changed", "data": [
          {
            "object": "services.identrequests",
            "id": "XXX_XXXXXXXXXXXXXXXXXXXXXXXX"
          }
        ]} */

      boolean ok = client.handleEvent(jsonEventData);
      // done

    } finally {
      // important to close the client properly at last, avoids leaking resources
      client.disconnect();
    }
  }
}
