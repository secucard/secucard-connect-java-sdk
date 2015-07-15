package com.secucard.connect.product.payment;

import com.secucard.connect.client.Callback;
import com.secucard.connect.client.ProductService;
import com.secucard.connect.product.payment.model.Contract;

public class ContractService extends ProductService<Contract> {

  @Override
  protected ServiceMetaData<Contract> createMetaData() {
    return new ServiceMetaData<>("payment", "contracts", Contract.class);
  }

  public Contract cloneMyContract(String contractId, Contract.CloneParams params, Callback<Contract> callback) {
    return clone("me", params, callback);
  }

  public Contract clone(String contractId, Contract.CloneParams params, Callback<Contract> callback) {
    return execute(contractId, "clone", null, params, Contract.class, null, callback);
  }
}
