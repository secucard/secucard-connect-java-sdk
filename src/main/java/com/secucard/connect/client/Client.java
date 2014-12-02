package com.secucard.connect.client;

import com.secucard.connect.SecuException;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.event.Events;
import com.secucard.connect.service.AbstractService;
import com.secucard.connect.service.ServiceFactory;

import java.io.IOException;

/**
 * Main entry to the Java Secucard Connect API.
 */
public class Client extends AbstractService implements EventListener {
  protected volatile boolean isConnected;
  private Thread heartbeatInvoker;
  private String id;
  private ServiceFactory serviceFactory;
  private EventListener targetEventListener = null;

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
   * @param <T>          The service type.
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

  public void setEventListener(final EventListener eventListener) {
    targetEventListener = eventListener;
  }

  public void removeEventListener() {
    targetEventListener = null;
  }

  public void connect() {
    try {
      // first rest since it does auth
      getRestChannel().open(null);
      getStompChannel().open(null);
      startHeartBeat();
    } catch (IOException e) {
      handleException(e);
    }
  }

  public void disconnect() {
    stopHeartBeat();
    getStompChannel().close(null);
    getRestChannel().close(null);
    // todo: clear data store?
  }

  public boolean isConnected() {
    return isConnected;
  }

  /**
   * Performes basic stomp event handling, delegates event to another target listener after.
   *
   * @param event
   */
  @Override
  public void onEvent(Object event) {
    handleEvent(event);
    if (targetEventListener != null) {
      targetEventListener.onEvent(event);
    }
  }

  private void startHeartBeat() {
    final int heartBeatSec = context.getConfig().getHeartBeatSec();
    if (heartBeatSec != 0) {
      stopHeartBeat();
      heartbeatInvoker = new Thread() {
        @Override
        public void run() {
          while (!isInterrupted()) {
            try {
              getStompChannel().invoke("ping", null);
            } catch (Exception e) {
              handleException(e);
              break;
            }
            try {
              Thread.sleep(heartBeatSec * 1000);
            } catch (InterruptedException e) {
              handleException(new Exception("Stomp heart beat stopped.", e));
              break;
            }
          }
        }
      };
      heartbeatInvoker.start();
    }
  }

  private void stopHeartBeat() {
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
    isConnected = false;

    getStompChannel().setEventListener(this);

    // bubble up ex. for now todo: forward to handler
    setExceptionHandler(new ExceptionHandler() {
      @Override
      public void handle(Exception exception) {
        throw new SecuException(exception);
      }
    });
  }

  private void handleEvent(Object event) {
    if (Events.CONNECTED.equals(event)) {
      isConnected = true;
      LOG.info("Connected to server.");
    } else if (Events.DISCONNECTED.equals(event)) {
      isConnected = false;
      LOG.info("Disconnected from server.");
    }
  }

  public void setExceptionHandler(ExceptionHandler exceptionHandler) {
    context.setExceptionHandler(exceptionHandler);
  }


}
