package com.secucard.connect.product.payment;

/**
 * Holds service references and service type constants for "payment" product.
 */
public class Payment {

  public static Class<ContainersService> Containers = ContainersService.class;
  public static Class<ContractsService> Contracts = ContractsService.class;
  public static Class<CustomersService> Customers = CustomersService.class;
  public static Class<SecupayDebitsService> Secupaydebits = SecupayDebitsService.class;
  public static Class<SecupayPrepaysService> Secupayprepays = SecupayPrepaysService.class;
  public static Class<SecupayInvoicesService> Secupayinvoices = SecupayInvoicesService.class;
  public static Class<SecupayCreditcardsService> Secupaycreditcards = SecupayCreditcardsService.class;
  public static Class<SecupayPayoutService> Secupaypayout = SecupayPayoutService.class;

  public ContainersService containers;
  public ContractsService contracts;
  public CustomersService customers;
  public SecupayDebitsService secupaydebits;
  public SecupayPrepaysService secupayprepays;
  public SecupayInvoicesService secupayinvoices;
  public SecupayCreditcardsService secupaycreditcards;
  public SecupayPayoutService secupaypayout;

  public Payment(ContainersService containers, CustomersService customers, SecupayDebitsService secupaydebits, SecupayPrepaysService secupayprepays,
      ContractsService contracts, SecupayInvoicesService secupayinvoices, SecupayCreditcardsService secupaycreditcards,
      SecupayPayoutService secupaypayout) {
    this.containers = containers;
    this.customers = customers;
    this.secupaydebits = secupaydebits;
    this.secupayprepays = secupayprepays;
    this.contracts = contracts;
    this.secupayinvoices = secupayinvoices;
    this.secupaycreditcards = secupaycreditcards;
    this.secupaypayout = secupaypayout;
  }

}
