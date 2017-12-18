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

package com.secucard.connect.product.loyalty;

/**
 * Holds service references and service type constants for "loyalty" product.
 */
public class Loyalty {

  public Loyalty(CardsService cards, CustomersService customers, MerchantCardsService merchantcards, CardGroupsService cardgroups) {
    this.cards = cards;
    this.cardgroups = cardgroups;
    this.customers = customers;
    this.merchantcards = merchantcards;
  }

  public static Class<CardsService> Cards = CardsService.class;
  public CardsService cards;

  public static Class<CardGroupsService> CardGroups = CardGroupsService.class;
  public CardGroupsService cardgroups;

  public static Class<CustomersService> Customers = CustomersService.class;
  public CustomersService customers;

  public static Class<MerchantCardsService> Merchantcards = MerchantCardsService.class;
  public MerchantCardsService merchantcards;

}
