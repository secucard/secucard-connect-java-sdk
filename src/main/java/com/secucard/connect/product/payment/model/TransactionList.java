package com.secucard.connect.product.payment.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Holds Payment basket details.
 */
public class TransactionList {

  public static final String ITEM_TYPE_TRANSACTION_PAYOUT = "transaction_payout";

  private String name;

  private int total;

  @JsonProperty("item_type")
  private String itemType = ITEM_TYPE_TRANSACTION_PAYOUT;

  @JsonProperty("transaction_hash")
  private String transactionHash;


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getTotal() {
    return total;
  }

  public void setTotal(int total) {
    this.total = total;
  }

  public String getItemType() {
    return itemType;
  }

  public void setItemType(String itemType) {
    this.itemType = itemType;
  }

  public String getTransactionHash() {
    return transactionHash;
  }

  public void setTransactionHash(String transactionHash) {
    this.transactionHash = transactionHash;
  }

  @Override
  public String toString() {
    return "TransactionList{" + "name='" + getName() + '\'' + ", total='" + getTotal() + '\'' + ", itemType='" + getItemType() + '\''
        + ", transactionHash='" + getTransactionHash() + '\'' + '}';
  }

}
