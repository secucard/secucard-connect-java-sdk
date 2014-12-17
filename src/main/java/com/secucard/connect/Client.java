package com.secucard.connect;

import com.secucard.connect.auth.OAuthUserCredentials;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.event.Events;
import com.secucard.connect.service.AbstractService;
import com.secucard.connect.service.ServiceFactory;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.logging.Level;

/**
 * Main entry to the Java Secucard Connect API.
 */
public class Client extends AbstractService implements EventListener {
  protected volatile boolean isConnected;
  private Thread heartbeatInvoker;
  private String id;
  private ServiceFactory serviceFactory;
  private EventListener targetEventListener = null;
  private boolean stopHeartbeat;

  private Client(final String id, ClientConfiguration configuration, Object runtimeContext) {
    init(id, configuration, runtimeContext);
  }


  public static Client create(String id, ClientConfiguration configuration) {
    return new Client(id, configuration, null);
  }

  /**
   * Creating a client instance for accessing the API.
   * The returned client implements just basic operations like opening and closing resources.
   * To access business related operations obtain a service instance from this client
   * via {@link #create(String, ClientConfiguration)} method.
   *
   * @param id             A unique id associated with this client.
   * @param configuration  The configuration of the client.
   * @param runtimeContext Any context object needed to create services etc., accessible later on via ClientContext.
   * @return The client instance.
   */
  public static Client create(String id, ClientConfiguration configuration, Object runtimeContext) {
    return new Client(id, configuration, runtimeContext);
  }

  public void setUserCredentials(String user, String pwd) {
    context.getConfig().setUserCredentials(new OAuthUserCredentials(user, pwd));
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
      handleException(e, null);
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
      stopHeartbeat = false;
      heartbeatInvoker = new Thread() {
        @Override
        public void run() {
          if (LOG.isLoggable(Level.INFO)) {
            LOG.info("stomp heart beat started (" + heartBeatSec + "s).");
          }
          while (!stopHeartbeat) {
            try {
              getStompChannel().invoke("ping", null);
            } catch (Exception e) {
              handleException(new SecuException("Error sending heart beat message.", e), null);
              break;
            }
            try {
              Thread.sleep(heartBeatSec * 1000);
            } catch (InterruptedException e) {
              break;
            }
          }
          if (LOG.isLoggable(Level.INFO)) {
            LOG.info("stomp heart beat stopped");
          }
        }
      };
      heartbeatInvoker.start();
    }
  }

  public void handleConnectionStateChanged() {

  }

  private void stopHeartBeat() {
    if (heartbeatInvoker != null) {
      stopHeartbeat = true;
      try {
        heartbeatInvoker.join();
      } catch (InterruptedException e) {
      }
    }
  }

  private void init(String id, ClientConfiguration config, Object runtimeContext) {
    if (config == null) {
      throw new SecuException("Configuration  must not be null.");
    }
    this.id = id;
    context = new ClientContext();
    context.setConfig(config);
    context.setClientId(id);
    context.setRuntimeContext(runtimeContext);
    String serviceFactoryName = config.getServiceFactory();
    if (StringUtils.isNotBlank(serviceFactoryName)) {
      try {
        Class<?> sfc = Class.forName(serviceFactoryName);
        serviceFactory = (ServiceFactory) sfc.newInstance();
      } catch (Exception e) {
        throw new SecuException("Cannnot instantiate service factory " + serviceFactoryName, e);
      }
    } else {
      serviceFactory = new ServiceFactory();
    }
    serviceFactory.init(context);
    isConnected = false;

    getStompChannel().setEventListener(this);

    // bubble up ex. for now todo: forward to handler
    setExceptionHandler(new ThrowingExceptionHandler());
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


  private static class ThrowingExceptionHandler implements ExceptionHandler {
    @Override
    public void handle(Exception exception) {
      throw new SecuException(exception);
    }
  }
}
