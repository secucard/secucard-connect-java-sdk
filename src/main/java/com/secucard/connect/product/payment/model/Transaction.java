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
import com.secucard.connect.product.common.model.SecuObject;

import java.util.Currency;

public abstract class Transaction extends SecuObject {
  public static final String STATUS_ACCEPTED = "accepted"; // status for accepted debit transactions and finished prepay transactions
  public static final String STATUS_AUTHORIZED = "authorized"; // prepay transaction after creation , before payment arrives
  public static final String STATUS_DENIED = "denied"; // when scoring for debit transaction denies the payer
  public static final String STATUS_ISSUE = "issue"; // then ruecklastschrift happens, or some other issue type
  public static final String STATUS_VOID = "void"; // when transaction is cancelled by creator (it is not possible to cancel transactions any time, so the debit transaction is possible to cancel until it is cleared out)
  public static final String STATUS_ISSUE_RESOLVED = "issue_resolved"; // when issue for transaction is resolved
  public static final String STATUS_REFUND = "refund"; // special status, saying that transaction was paid back (for some reason)
  public static final String STATUS_INTERNAL_SERVER_STATUS = "internal_server_status"; // should not happen, but only when status would be empty, this status is used

  protected Customer customer;

  protected Contract contract;

  protected long amount;

  protected Currency currency;

  protected String purpose;

  @JsonProperty("order_id")
  protected String orderId;

  @JsonProperty("trans_id")
  protected String transId;

  protected String status;

  @JsonProperty("transaction_status")
  protected String transactionStatus;

  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  public Contract getContract() {
    return contract;
  }

  public void setContract(Contract contract) {
    this.contract = contract;
  }

  public long getAmount() {
    return amount;
  }

  public void setAmount(long amount) {
    this.amount = amount;
  }

  public Currency getCurrency() {
    return currency;
  }

  public void setCurrency(Currency currency) {
    this.currency = currency;
  }

  public String getPurpose() {
    return purpose;
  }

  public void setPurpose(String purpose) {
    this.purpose = purpose;
  }

  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

  public String getTransId() {
    return transId;
  }

  public void setTransId(String transId) {
    this.transId = transId;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getTransactionStatus() {
    return transactionStatus;
  }

  public void setTransactionStatus(String transactionStatus) {
    this.transactionStatus = transactionStatus;
  }

}
