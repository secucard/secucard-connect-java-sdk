package com.secucard.connect.product.general;

/**
 * Holds service references and service type constants for "general" product.
 */
public class General {

  public General(AccountDevicesService accountdevices, AccountsService accounts, MerchantsService merchants,
                 NewsService news, PublicMerchantsService publicmerchants, StoresService stores,
                 TransactionsService transactions) {
    this.accountdevices = accountdevices;
    this.accounts = accounts;
    this.merchants = merchants;
    this.news = news;
    this.publicmerchants = publicmerchants;
    this.stores = stores;
    this.transactions = transactions;
  }

  public static Class<AccountDevicesService> Accountdevices = AccountDevicesService.class;
  public AccountDevicesService accountdevices;

  public static Class<AccountsService> Accounts = AccountsService.class;
  public AccountsService accounts;


  public static Class<MerchantsService> Merchants = MerchantsService.class;
  public MerchantsService merchants;

  public static Class<NewsService> News = NewsService.class;
  public NewsService news;

  public static Class<PublicMerchantsService> Publicmerchants = PublicMerchantsService.class;
  public PublicMerchantsService publicmerchants;

  public static Class<StoresService> Stores = StoresService.class;
  public StoresService stores;

  public static Class<TransactionsService> Transactions = TransactionsService.class;
  public TransactionsService transactions;

}
