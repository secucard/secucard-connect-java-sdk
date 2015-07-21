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
