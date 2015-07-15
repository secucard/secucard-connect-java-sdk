package com.secucard.connect.product.loyalty;

import com.secucard.connect.product.general.MerchantsService;

/**
 * Holds service references and service type constants for "loyalty" product.
 */
public class Loyalty {

  public Loyalty(CardsService cards, CustomersService customers, MerchantCardsService merchantcards) {
    this.cards = cards;
    this.customers = customers;
    this.merchantcards = merchantcards;
  }

  public static Class<CardsService> Cards = CardsService.class;
  public CardsService cards;

  public static Class<CustomersService> Customers = CustomersService.class;
  public CustomersService customers;


  public static Class<MerchantCardsService> Merchantcards = MerchantCardsService.class;
  public MerchantCardsService merchantcards;


}
