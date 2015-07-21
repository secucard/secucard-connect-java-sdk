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

package com.secucard.connect.product.payment.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SecupayPrepay extends Transaction {
  @JsonProperty("transfer_purpose")
  private String transferPurpose;

  @JsonProperty("transfer_account")
  private TransferAccount transferAccount;

  public String getTransferPurpose() {
    return transferPurpose;
  }

  public void setTransferPurpose(String transferPurpose) {
    this.transferPurpose = transferPurpose;
  }

  public TransferAccount getTransferAccount() {
    return transferAccount;
  }

  public void setTransferAccount(TransferAccount transferAccount) {
    this.transferAccount = transferAccount;
  }

  @Override
  public String toString() {
    return "SecupayPrepay{" +
        "transferPurpose='" + transferPurpose + '\'' +
        ", transactionStatus='" + transactionStatus + '\'' +
        ", transferAccount=" + transferAccount +
        "} " + super.toString();
  }
}
