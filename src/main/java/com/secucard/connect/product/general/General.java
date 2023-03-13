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

package com.secucard.connect.product.general;

/**
 * Holds service references and service type constants for "general" product.
 */
public class General {

  public General(AccountDevicesService accountdevices, AccountsService accounts, AppsService apps, MerchantsService merchants, NewsService news,
      PublicMerchantsService publicmerchants, StoresService stores, TransactionsService transactions) {
    this.accountdevices = accountdevices;
    this.accounts = accounts;
    this.apps = apps;
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

  public static Class<AppsService> Apps = AppsService.class;
  public AppsService apps;

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
