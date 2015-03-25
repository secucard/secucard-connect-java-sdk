package com.secucard.connect;

import com.secucard.connect.event.EventListener;
import com.secucard.connect.model.general.Address;
import com.secucard.connect.model.general.Contact;
import com.secucard.connect.model.services.IdentRequest;
import com.secucard.connect.model.services.IdentResult;
import com.secucard.connect.model.services.idrequest.Person;
import com.secucard.connect.service.services.IdentService;

import java.text.SimpleDateFormat;
import java.util.Arrays;
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
      Contact c = new Contact();
      c.setForename("Hans");
      c.setSurname("Dampf");
      c.setGender(Contact.GENDER_MALE);
      c.setNationality(Locale.GERMANY);
      c.setDateOfBirth(new SimpleDateFormat("dd.MM.yyyy").parse("01.01.1951"));
      Address a = new Address();
      a.setCity("Berlin");
      a.setPostalCode("11011");
      a.setCountry(Locale.GERMANY);
      a.setStreet("Platz der Republik");
      a.setStreetNumber("1");

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

        /**
         *  If returning true all ident result attachments (pdf, etc) will be downloaded BEFORE completed() method is
         *  called, later access to an attachment (pdf, etc.) will be served from cache.
         *  (Access to attachment is like: result.getPersons().get(0).getAttachments().get(0).getInputStream())
         *
         *  Returning false prevents such eager attachment download at all, attachments are downloaded and cached on
         *  the fly when accessed (lazy).
         *
         *  Depending on the amount of data to download eager loading may be a good idea or not (considering also if
         *  all of the downloaded data will be actually accessed).
         */
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