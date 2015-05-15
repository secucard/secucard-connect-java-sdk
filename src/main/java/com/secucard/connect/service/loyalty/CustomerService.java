package com.secucard.connect.service.loyalty;

import com.secucard.connect.Callback;
import com.secucard.connect.model.loyalty.Customer;
import com.secucard.connect.service.AbstractService;

public class CustomerService extends AbstractService {

  /**
   * Return a loyalty customer.
   *
   * @param id       Th customers id.
   * @param callback Callback for async processing.
   * @return The customer object. Never null, a {@link com.secucard.connect.ServerErrorException} is thrown instead if the
   * give id was invalid.
   */
  public Customer getCustomer(String id, Callback<Customer> callback) {
    return new ServiceTemplate().get(Customer.class, id, callback);
  }
}
