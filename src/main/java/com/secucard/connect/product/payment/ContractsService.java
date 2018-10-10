package com.secucard.connect.product.payment;

import com.secucard.connect.client.Callback;
import com.secucard.connect.client.ProductService;
import com.secucard.connect.product.payment.model.Contract;

/**
 * Implements the payment/contracts operations.
 */
public class ContractsService extends ProductService<Contract> {

  public static final ServiceMetaData<Contract> META_DATA = new ServiceMetaData<>("payment",
      "contracts", Contract.class);

  @Override
  public ServiceMetaData<Contract> getMetaData() {
    return META_DATA;
  }

  /**
   * Clones the contract of the current user according to the given parameters and returns the
   * contract.
   */
  public Contract cloneMyContract(Contract.CloneParams params, Callback<Contract> callback) {
    return clone("me", params, callback);
  }

  /**
   * Clones a contract with a given id according to the given parameters and returns the contract.
   */
  public Contract clone(String contractId, Contract.CloneParams params,
      Callback<Contract> callback) {
    return execute(contractId, "clone", null, params, Contract.class, null, callback);
  }
}
