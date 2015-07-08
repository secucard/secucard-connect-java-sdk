package com.secucard.connect.service.payment;

import com.secucard.connect.Callback;
import com.secucard.connect.model.payment.CloneParams;
import com.secucard.connect.model.payment.Contract;
import com.secucard.connect.service.AbstractService;

public class ContractService extends AbstractService {

  public Contract cloneMyContract(CloneParams params, Callback<Contract> callback) {
    return cloneContract("me", params, callback);
  }

  public Contract cloneContract(String contractId, CloneParams params, Callback<Contract> callback) {
    return execute(Contract.class, contractId, "clone", null, params, Contract.class, callback, null);
  }
}
