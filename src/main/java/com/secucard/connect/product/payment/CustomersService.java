package com.secucard.connect.product.payment;

import com.secucard.connect.client.ProductService;
import com.secucard.connect.product.payment.model.Customer;

/**
 * Implements the payment/customers operations.
 */
public class CustomersService extends ProductService<Customer> {

  public static final ServiceMetaData<Customer> META_DATA = new ServiceMetaData<>("payment",
      "customers", Customer.class);

  @Override
  public ServiceMetaData<Customer> getMetaData() {
    return META_DATA;
  }
}
