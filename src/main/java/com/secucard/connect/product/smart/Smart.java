package com.secucard.connect.product.smart;

public class Smart {

  public Smart(CheckinService checkins, IdentService idents, TransactionService transactions) {
    this.checkins = checkins;
    this.idents = idents;
    this.transactions = transactions;
  }

  public static Class<CheckinService> Checkins = CheckinService.class;
  public CheckinService checkins;

  public static Class<IdentService> Idents = IdentService.class;
  public IdentService idents;

  public static Class<TransactionService> Transactions = TransactionService.class;
  public TransactionService transactions;
}
