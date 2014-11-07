package com.secucard.connect.example.service;

import com.secucard.connect.EventListener;
import com.secucard.connect.model.general.Event;
import com.secucard.connect.model.general.skeleton.Skeleton;
import com.secucard.connect.model.smart.*;

import java.util.List;

public class Main implements EventListener {

  public static void main(String[] args) throws Exception{

    // service style API
    // services implement most of the business behaviour,
    // domain model objects hold just data or implement just "lightweight" operations like calculations or list traversal
    // name it service or factory or whatever - the principle is the same


    SmartService smartService = SmartService.create(null);

    GeneralService generalService = GeneralService.create(null);

    smartService.setEventListener(new Main());

    smartService.connect();
    generalService.connect();

    try {
      boolean ok = smartService.registerDevice(new Device("me", "cashier"));

      List<Ident> idents = smartService.getIdents();

      Basket basket = new Basket();
      BasketInfo basketInfo = new BasketInfo(19.99f, "euro");
      Transaction transaction = new Transaction();
      transaction.setBasket(basket);
      transaction.setBasketInfo(basketInfo);

      Transaction res = smartService.createTransaction(transaction);


      Result result = smartService.startTransaction(transaction);


      Skeleton skeleton = generalService.getSkeleton("skl_60");

    } finally {
      smartService.disconnect();
      generalService.disconnect();
    }


  }

  public void onEvent(Event event) {
    System.out.println("Event: " + event);
  }
}
