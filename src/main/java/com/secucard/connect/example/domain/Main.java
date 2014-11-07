package com.secucard.connect.example.domain;

import com.secucard.connect.EventListener;
import com.secucard.connect.model.general.Event;
import com.secucard.connect.model.smart.Basket;
import com.secucard.connect.model.smart.BasketInfo;
import com.secucard.connect.model.smart.Result;

public class Main implements EventListener{

  public static void main(String[] args) throws Exception{

    // domain driven style API
    // domain models implement behaviour AND data
    // looks good, pure OI, but has heavy downsides:
    // no "plain" java objects,
    // models have dependencies to infrastructure and must be initialized and destroyed carefully
    // depending on the resources a model holds a model should be reused instead of destroyed (singleton)
    // etc.

    Device device = Device.create(null);
    device.connect();
    device.setId("me");
    device.setType("cahsier");
    device.register();
    device.disconnect();

    Transaction transaction = Transaction.create(null);
    transaction.setEventListener(new Main());
    transaction.connect();

    Basket basket = new Basket();
    BasketInfo basketInfo = new BasketInfo(19.99f, "euro");
    transaction.setBasket(basket);
    transaction.setBasketInfo(basketInfo);

    Transaction saved = transaction.save();

    Result result = transaction.start();

    transaction.disconnect();

  }

  public void onEvent(Event event) {
    System.out.println("Event: " + event);
  }
}
