package com.secucard.connect.service.payment;

import com.secucard.connect.Callback;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.QueryParams;
import com.secucard.connect.model.payment.Customer;
import com.secucard.connect.service.AbstractService;

public class CustomerService extends AbstractService {

  public ObjectList<Customer> getCustomers(QueryParams queryParams, final Callback<ObjectList<Customer>> callback) {
    try {
      ObjectList<Customer> objects = getRestChannel().findObjects(Customer.class, queryParams,
          callback);
      return objects;
    } catch (Exception e) {
      handleException(e, callback);
    }
    return null;
  }

  public Customer createCustomer(final Customer customer, Callback<Customer> callback) {
    return new Invoker<Customer>() {
      @Override
      protected Customer handle(Callback<Customer> callback) throws Exception {
        return getChannel().createObject(customer, callback);
      }
    }.invoke(callback);
  }

  public Customer updateCustomer(final Customer customer, Callback<Customer> callback) {
    return new Invoker<Customer>() {
      @Override
      protected Customer handle(Callback<Customer> callback) throws Exception {
        return getChannel().updateObject(customer, callback);
      }
    }.invoke(callback);
  }

  public void deleteCustomer(final String id, Callback callback) {
    new Invoker<Void>() {
      @Override
      protected Void handle(Callback<Void> callback) throws Exception {
        getChannel().deleteObject(Customer.class, id, callback);
        return null;
      }
    }.invoke(callback);
  }


}
