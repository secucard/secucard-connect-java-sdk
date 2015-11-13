/*
 * Copyright (c) 2015. hp.weber GmbH & Co secucard KG (www.secucard.com)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.secucard.connect.product.payment;

/**
 * Holds service references and service type constants for "payment" product.
 */
public class Payment {

  public Payment(ContainersService containers, CustomersService customers, SecupayDebitsService secupaydebits,
                 SecupayPrepaysService secupayprepays, ContractsService contracts) {
    this.containers = containers;
    this.customers = customers;
    this.secupaydebits = secupaydebits;
    this.secupayprepays = secupayprepays;
    this.contracts = contracts;
  }

  public static Class<ContainersService> Containers = ContainersService.class;
  public ContainersService containers;

  public static Class<ContractsService> Contracts = ContractsService.class;
  public ContractsService contracts;

  public static Class<CustomersService> Customers = CustomersService.class;
  public CustomersService customers;

  public static Class<SecupayDebitsService> Secupaydebits = SecupayDebitsService.class;
  public SecupayDebitsService secupaydebits;

  public static Class<SecupayPrepaysService> Secupayprepays = SecupayPrepaysService.class;
  public SecupayPrepaysService secupayprepays;
}
