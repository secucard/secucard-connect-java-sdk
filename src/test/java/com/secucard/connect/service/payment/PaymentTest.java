package com.secucard.connect.service.payment;

import com.secucard.connect.model.QueryParams;
import com.secucard.connect.model.general.Merchant;
import com.secucard.connect.model.payment.Container;
import com.secucard.connect.model.payment.Customer;
import com.secucard.connect.model.payment.Data;
import com.secucard.connect.service.AbstractServicesTest;
import org.junit.Assert;

import java.text.SimpleDateFormat;
import java.util.List;

public class PaymentTest extends AbstractServicesTest {

  @Override
  protected void executeTests() throws Exception {
    CustomerService customerService = client.getService(CustomerService.class);
    ContainerService containerService = client.getService(ContainerService.class);

    Customer customer = new Customer();
    customer.setCity("city");
    customer.setCompanyName("companyname");
    customer.setDateOfBirth(new SimpleDateFormat("dd.MM.yyyy").parse("1.4.1888"));
    customer.setEmail("email");
    customer.setForeName("forename");
    customer.setSurName("surname");
    customer.setMerchant(new Merchant());
    customer.setPhone("110");
    customer.setStreet("street");

    customer = customerService.createCustomer(customer, null);
    QueryParams queryParams = new QueryParams();
    queryParams.setQuery("id:" + customer.getId());
    List<Customer> customers = customerService.getCustomers(queryParams, null);
    Assert.assertTrue(customers.size() == 1);


    String name = "hans";
    customer.setForeName(name);
    customer = customerService.updateCustomer(customer, null);
    Assert.assertTrue(customer.getForeName().equals(name));


    Container container = new Container();
    container.setType(Container.TYPE_BANK_ACCOUNT);
    container.setPrivateData(new Data("owner", "iban", "bic"));


    container = containerService.createContainer(container, null);
    queryParams = new QueryParams();
    queryParams.setQuery("id:" + container.getId());
    List<Container> containers = containerService.getContainers(queryParams, null);
    Assert.assertTrue(containers.size() == 1);


    String owner = "owner2";
    container.setPrivateData(new Data(owner, "iban2", "bic2"));
    container = containerService.updateContainer(container, null);
    Assert.assertTrue(container.getPublicData().getOwner().equals(owner));


    container = containerService.updateContainerAssignment(container.getId(), customer.getId(), null);
    Assert.assertTrue(container.getAssigned().getId().equals(customer.getId()));


    containerService.deleteContainerAssignment(container.getId(), null);
    containers = containerService.getContainers(queryParams, null);
    Assert.assertTrue(containers.get(0).getAssigned() == null);


    containerService.deleteContainer(container.getId(), null);
    containers = containerService.getContainers(queryParams, null);
    Assert.assertTrue(containers == null);


    customerService.deleteCustomer(customer.getId(), null);
    customers = customerService.getCustomers(queryParams, null);
    Assert.assertNull(customers);
  }
}
