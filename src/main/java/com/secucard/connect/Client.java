package com.secucard.connect;

import com.secucard.connect.auth.UserCredentials;
import com.secucard.connect.channel.stomp.StompChannel;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.event.Events;
import com.secucard.connect.service.AbstractService;
import com.secucard.connect.service.ServiceFactory;
import com.secucard.connect.storage.DataStorage;

import java.util.logging.Level;

/**
 * Main entry to the Java Secucard Connect API.
 */
public class Client extends AbstractService implements EventListener {
  protected volatile boolean isConnected;
  private Thread heartbeatInvoker;
  private String id;
  private ServiceFactory serviceFactory;
  private boolean stopHeartbeat;


  private Client(final String id, ClientConfiguration configuration, Object runtimeContext, DataStorage storage) {
    init(id, configuration, runtimeContext, storage);
  }


  /**
   * Creating the client using no runtime context and storage.
   *
   * @see #Client(String, ClientConfiguration, Object, com.secucard.connect.storage.DataStorage)
   */
  public static Client create(String id, ClientConfiguration configuration) {
    return new Client(id, configuration, null, null);
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
   *                       Mainly intended for Android usage, pass Application context then. Pass null else.
   * @param storage        The DataStorage implementation the client uses for caching purposes. Passing null causes usage of
   *                       default internal solutions. While this is ok for Android it should be avoided in other environments.
   *                       Please provide a proper implementation there, the default is either a memory based solution or a
   *                       very simple file based solution if the "cacheDir" property is set in config file.
   * @return The client instance.
   */
  public static Client create(String id, ClientConfiguration configuration, Object runtimeContext, DataStorage storage) {
    return new Client(id, configuration, runtimeContext, storage);
  }


  public void setUserCredentials(String user, String pwd) {
    context.getConfig().setUserCredentials(new UserCredentials(user, pwd));
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

  public synchronized void connect() {
    if (isConnected) {
      return;
    }
    try {
      getRestChannel().open(null); // init rest first since it does auth,
      if (context.getConfig().isStompEnabled()) {
        getStompChannel().open(null);
        startHeartBeat();
      }
    } catch (Exception e) {
      isConnected = false;
      handleException(e, null);
    }
    isConnected = true;
    context.getEventDispatcher().fireEvent(new Events.ConnectionStateChanged(true));
  }

  public synchronized void disconnect() {
    try {
      if (context.getConfig().isStompEnabled()) {
        stopHeartBeat();
        getStompChannel().close(null);
      }
      getRestChannel().close(null);
      clear();
      // todo: clear data store?
    } finally {
      isConnected = false;
      context.getEventDispatcher().fireEvent(new Events.ConnectionStateChanged(false));
    }
  }

  public boolean isConnected() {
    return isConnected;
  }

  /**
   * Client can act itself as an event listener, for instance to stomp events,
   * see {@link #init(String, ClientConfiguration, Object, DataStorage)}.
   * This propagates this events via dispatcher to other listeners registered to the dispatcher after handling them.
   * todo: introduce separate listener like an event source
   */
  @Override
  public void onEvent(Object event) {
    handleEvent(event);
    context.getEventDispatcher().fireEvent(event);
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
              ((StompChannel) getStompChannel()).ping();
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

  private void stopHeartBeat() {
    if (heartbeatInvoker != null) {
      stopHeartbeat = true;
      try {
        heartbeatInvoker.join();
      } catch (InterruptedException e) {
      }
    }
  }

  private void init(String id, ClientConfiguration config, Object runtimeContext, DataStorage storage) {
    if (config == null) {
      throw new SecuException("Configuration  must not be null.");
    }

    this.id = id;
    context = new ClientContext(id, config, runtimeContext, storage);
    serviceFactory = new ServiceFactory(context);
    isConnected = false;

    // set up event sources
    if (config.isStompEnabled()) {
      getStompChannel().setEventListener(this);
    }

    // bubble up ex. for now todo: forward to handler
    setExceptionHandler(new ThrowingExceptionHandler());
  }

  private void handleEvent(Object event) {
    if (Events.CONNECTED.equals(event)) {
      LOG.info("Connected to server.");
    } else if (Events.DISCONNECTED.equals(event)) {
      LOG.info("Disconnected from server.");
    }
  }

  public void setExceptionHandler(ExceptionHandler exceptionHandler) {
    context.setExceptionHandler(exceptionHandler);
  }


  private static class ThrowingExceptionHandler implements ExceptionHandler {
    @Override
    public void handle(Throwable exception) {
      throw new SecuException(exception);
    }
  }
}
