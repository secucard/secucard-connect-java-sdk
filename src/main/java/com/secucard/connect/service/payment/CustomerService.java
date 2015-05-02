package com.secucard.connect.service.payment;

import com.secucard.connect.Callback;
import com.secucard.connect.model.QueryParams;
import com.secucard.connect.model.payment.Customer;
import com.secucard.connect.service.AbstractService;

import java.util.List;

public class CustomerService extends AbstractService {

  public List<Customer> getCustomers(QueryParams queryParams, final Callback<List<Customer>> callback) {
    return new ServiceTemplate().getAsList(Customer.class, queryParams, callback);
  }

  public Customer createCustomer(final Customer customer, Callback<Customer> callback) {
    return new ServiceTemplate().create(customer, callback);
  }

  public Customer updateCustomer(final Customer customer, Callback<Customer> callback) {
    return new ServiceTemplate().update(customer, callback);
  }

  public void deleteCustomer(final String id, Callback callback) {
    new ServiceTemplate().delete(Customer.class, id, callback);
  }
}
