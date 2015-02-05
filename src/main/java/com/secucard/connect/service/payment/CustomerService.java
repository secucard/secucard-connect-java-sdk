package com.secucard.connect.service.payment;

import com.secucard.connect.Callback;
import com.secucard.connect.model.QueryParams;
import com.secucard.connect.model.payment.Customer;
import com.secucard.connect.service.AbstractService;

import java.util.List;

public class CustomerService extends AbstractService {

  public List<Customer> getCustomers(QueryParams queryParams, final Callback<List<Customer>> callback) {
    return get(Customer.class, queryParams, callback, null);
  }

  public Customer createCustomer(final Customer customer, Callback<Customer> callback) {
    return create(customer, callback, null);
  }

  public Customer updateCustomer(final Customer customer, Callback<Customer> callback) {
    return update(customer, callback, null);
  }

  public void deleteCustomer(final String id, Callback callback) {
    delete(Customer.class, id, callback, null);
  }
}
