package com.secucard.connect.auth;

import com.secucard.connect.Callback;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.service.AbstractServicesTest;

public class AuthTest extends AbstractServicesTest {

  @Override
  public void before() throws Exception {
    super.before();
    client.onEvent(new EventListener() {
      @Override
      public void onEvent(Object event) {
        System.out.println("AUTH EVENT: " + event);
      }
    });
  }

  @Override
  public void test() throws Exception {

    new Thread(){
      @Override
      public void run() {
        try {
          try {
            Thread.sleep(5000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          client.cancelAuth();
        } finally {
          client.disconnect();
        }

      }
    }.start();

    Callback<Void> cb = new Callback<Void>() {
      @Override
      public void completed(Void result) {
        System.out.println("CONNECTED");
      }

      @Override
      public void failed(Throwable cause) {
        System.err.print("FAILED: ");
        cause.printStackTrace();
      }
    };
    client.connect(null);


    Thread.sleep(10000);
  }
}
