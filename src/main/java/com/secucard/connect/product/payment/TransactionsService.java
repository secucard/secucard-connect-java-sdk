package com.secucard.connect.product.payment;

import com.secucard.connect.client.ProductService;
import com.secucard.connect.product.payment.model.Transactions;

/**
 * Implements the payment/transactions operations.
 */
public class TransactionsService extends ProductService<Transactions> {

  public static final ServiceMetaData<Transactions> META_DATA = new ServiceMetaData<>("payment", "transactions", Transactions.class);

  @Override
  public ServiceMetaData<Transactions> getMetaData() {
    return META_DATA;
  }
}
