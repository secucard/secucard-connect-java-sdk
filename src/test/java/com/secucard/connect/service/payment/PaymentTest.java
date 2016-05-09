package com.secucard.connect.service.payment;

import com.secucard.connect.model.QueryParams;
import com.secucard.connect.model.general.Address;
import com.secucard.connect.model.general.Contact;
import com.secucard.connect.model.general.Merchant;
import com.secucard.connect.model.payment.Container;
import com.secucard.connect.model.payment.Customer;
import com.secucard.connect.model.payment.Data;
import com.secucard.connect.service.AbstractServicesTest;
import org.junit.Assert;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PaymentTest extends AbstractServicesTest {

  @Override
  protected void executeTests() throws Exception {
    CustomerService customerService = client.getService(CustomerService.class);
    ContainerService containerService = client.getService(ContainerService.class);

    Customer customer = new Customer();
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
    customer.setContact(contact);
    customer.setMerchant(new Merchant());
    customer = customerService.createCustomer(customer, null);
    QueryParams queryParams = new QueryParams();
    queryParams.setQuery("id:" + customer.getId());
    List<Customer> customers = customerService.getCustomers(queryParams, null);
    Assert.assertTrue(customers.size() == 1);


    String name = "hans";
    customer.getContact().setForename(name);
    customer = customerService.updateCustomer(customer, null);
    Assert.assertTrue(customer.getContact().getForename().equals(name));

    Container container = new Container();
    container.setType(Container.TYPE_BANK_ACCOUNT);
    container.setPrivateData(new Data("iban"));
    container.setCustomer(customer);

    container = containerService.createContainer(container, null);
    queryParams = new QueryParams();
    queryParams.setQuery("id:" + container.getId());
    List<Container> containers = containerService.getContainers(queryParams, null);
    Assert.assertTrue(containers.size() == 1);
    Assert.assertTrue(container.getCustomer().getId().equals(customer.getId()));

    String owner = "owner2";
    container.setPrivateData(new Data("iban2"));
    container = containerService.updateContainer(container, null);
    Assert.assertTrue(container.getPublicData().getOwner().equals(owner));

    containerService.deleteContainer(container.getId(), null);
    containers = containerService.getContainers(queryParams, null);
    Assert.assertTrue(containers == null);

    customerService.deleteCustomer(customer.getId(), null);
    customers = customerService.getCustomers(queryParams, null);
    Assert.assertNull(customers);
  }
}
