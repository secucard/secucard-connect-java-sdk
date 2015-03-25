package com.secucard.connect;

import com.secucard.connect.event.EventListener;
import com.secucard.connect.model.general.Address;
import com.secucard.connect.model.general.Contact;
import com.secucard.connect.model.payment.Container;
import com.secucard.connect.model.payment.Customer;
import com.secucard.connect.model.payment.Data;
import com.secucard.connect.model.payment.SecupayDebit;
import com.secucard.connect.service.payment.ContainerService;
import com.secucard.connect.service.payment.CustomerService;
import com.secucard.connect.service.payment.SecupayDebitService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Currency;

public class PaymentDemo {

  public static void main(String[] args) throws IOException {

    final ClientConfiguration cfg = ClientConfiguration.fromProperties("config-paymentdemo.properties");
    final Client client = Client.create("paymentdemo", cfg);

    // setting any instance as a receiver for events the client produces, optional for this use case
    client.setEventListener(new EventListener() {
      @Override
      public void onEvent(Object event) {
        System.out.println("Got event: " + event);
      }
    });

    ContainerService containerService = client.getService(ContainerService.class);
    CustomerService customerService = client.getService(CustomerService.class);
    SecupayDebitService debitService = client.getService(SecupayDebitService.class);

    client.connect();

    try {

      Customer customer = new Customer();
      Contact contact = new Contact();
      contact.setForename("forename");
      contact.setSurname("surname");
      Address address = new Address();
      address.setCity("city");
      address.setStreet("street");
      contact.setAddress(address);
      customer.setContact(contact);
      // set more ...

      // create customer and get back filled up
      customer = customerService.createCustomer(customer, null);

      Container container = new Container();
      container.setType(Container.TYPE_BANK_ACCOUNT);
      container.setPrivateData(new Data("forename surname","iban", "bic"));

      // create container and get back filled up
      container = containerService.createContainer(container, null);

      SecupayDebit debit = new SecupayDebit();
      debit.setContainer(container);
      debit.setCustomer(customer);
      debit.setAmount(new BigDecimal(99.99));
      debit.setCurrency(Currency.getInstance("EUR"));
      debit.setOrderId("order1");
      debit.setPurpose("food");

      // pay, create transaction
      debit = debitService.createTransaction(debit, null);

      // process returned debit

      assert(debit.getStatus().equalsIgnoreCase("ok"));

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      // important to close the client properly at last, avoids leaking resources
      client.disconnect();
    }
  }
}
