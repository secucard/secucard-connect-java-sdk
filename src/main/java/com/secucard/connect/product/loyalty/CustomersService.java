package com.secucard.connect.product.loyalty;

import com.secucard.connect.client.ProductService;
import com.secucard.connect.product.loyalty.model.Customer;

public class CustomersService extends ProductService<Customer> {

  @Override
  public ServiceMetaData<Customer> createMetaData() {
    return new ServiceMetaData<>("loyalty", "customers", Customer.class);
  }

}
