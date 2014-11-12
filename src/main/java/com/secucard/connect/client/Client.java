package com.secucard.connect.client;

import com.secucard.connect.SecuException;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.service.AbstractService;
import com.secucard.connect.service.ServiceFactory;

import java.io.IOException;

/**
 * Main entry to the Java Secucard Connect API.
 */
public class Client {
  protected ClientContext context;
  private Thread heartbeatInvoker;
  private String id;
  private ServiceFactory serviceFactory;

  private Client(final String id, ClientConfiguration configuration) {
    init(id, configuration);
  }

  /**
   * Creating a client instance for accessing the API.
   * The returned client implements just basic operations like opening and closing resources.
   * To access business related operations obtain a service instance from this client
   * via {@link #create(String, ClientConfiguration)} method.
   *
   * @param id            A unique id associated with this client.
   * @param configuration The configuration of the client.
   * @return The client instance.
   */
  public static Client create(String id, ClientConfiguration configuration) {
    return new Client(id, configuration);
  }

  /**
   * Getting a new service instance from this client.
   * The returned instance offers several business related operation.
   * All returned services operate on the same resources of the client.
   *
   * @param serviceClass The actual service type.
   * @param <T>  The service type.
   * @return The service instance or null if not found.
   */
  public <T extends AbstractService> T getService(Class<T> serviceClass) {
    return serviceFactory.getService(serviceClass);
  }

  public <T extends AbstractService> T getService(String serviceId) {
    return serviceFactory.getService(serviceId);
  }

  public String getId() {
    return id;
  }

  public void setEventListener(EventListener eventListener) {
    context.getStompChannel().setEventListener(eventListener);
  }

  public void connect() throws ConnectException {
    try {
      // first rest since it does auth
      context.getRestChannel().open();
      context.getStompChannel().open();
      startHeartBeat();
    } catch (IOException e) {
      throw new ConnectException(e);
    }
  }

  public void disconnect() {
    stopHeartBeat();
    context.getStompChannel().close();
    context.getRestChannel().close();
    // todo: clear data store?
  }

  protected void startHeartBeat() {
    final int heartBeatSec = context.getConfig().getHeartBeatSec();
    if (heartBeatSec != 0) {
      stopHeartBeat();
      heartbeatInvoker = new Thread() {
        @Override
        public void run() {
          while (!isInterrupted()) {
            context.getStompChannel().invoke("/ping");
            try {

              Thread.sleep(heartBeatSec * 1000);
            } catch (InterruptedException e) {
              break;
            }
          }
        }
      };
      heartbeatInvoker.start();
    }
  }

  protected void stopHeartBeat() {
    if (heartbeatInvoker != null && heartbeatInvoker.isAlive()) {
      heartbeatInvoker.interrupt();
      try {
        heartbeatInvoker.join();
      } catch (InterruptedException e) {
      }
    }
  }

  private void init(String id, ClientConfiguration config) {
    if (config == null) {
      throw new SecuException("Configuration  must not be null.");
    }
    this.id = id;
    context = new ClientContext();
    context.setConfig(config);
    context.setClientId(id);
    serviceFactory = new ServiceFactory(context);
  }
}
