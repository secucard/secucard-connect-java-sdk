package com.secucard.connect.product.payment;

import com.secucard.connect.client.ProductService;
import com.secucard.connect.product.payment.model.Customer;

public class CustomersService extends ProductService<Customer> {

  @Override
  protected ServiceMetaData<Customer> createMetaData() {
    return new ServiceMetaData<>("payment", "customers", Customer.class);
  }
}
