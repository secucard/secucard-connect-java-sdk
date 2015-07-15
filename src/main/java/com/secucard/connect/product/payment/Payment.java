package com.secucard.connect.product.payment;

/**
 * Holds service references and service type constants for "payment" product.
 */
public class Payment {

  public Payment(ContainersService containers, CustomersService customers, SecupayDebitsService secupaydebits,
                 SecupayPrepaysService secupayprepays, ContractService contracts) {
    this.containers = containers;
    this.customers = customers;
    this.secupaydebits = secupaydebits;
    this.secupayprepays = secupayprepays;
    this.contracts = contracts;
  }

  public static Class<ContainersService> Containers = ContainersService.class;
  public ContainersService containers;

  public static Class<ContractService> Contracts = ContractService.class;
  public ContractService contracts;

  public static Class<CustomersService> Customers = CustomersService.class;
  public CustomersService customers;

  public static Class<SecupayDebitsService> Secupaydebits = SecupayDebitsService.class;
  public SecupayDebitsService secupaydebits;

  public static Class<SecupayPrepaysService> Secupayprepays = SecupayPrepaysService.class;
  public SecupayPrepaysService secupayprepays;
}
