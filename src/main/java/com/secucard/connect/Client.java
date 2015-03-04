package com.secucard.connect;

import com.secucard.connect.auth.UserCredentials;
import com.secucard.connect.channel.Channel;
import com.secucard.connect.channel.JsonMapper;
import com.secucard.connect.channel.stomp.StompChannel;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.event.Events;
import com.secucard.connect.model.general.Event;
import com.secucard.connect.service.AbstractService;
import com.secucard.connect.service.ServiceFactory;
import com.secucard.connect.storage.DataStorage;

import java.util.logging.Level;

/**
 * Main entry to the Java Secucard Connect API.
 */
public class Client extends AbstractService {
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

  /**
   * Initializes connects the client to the secucard server.<br/>
   * Throws {@link com.secucard.connect.auth.AuthException} if an error happens during authentication.<br/>
   * May also fire an event of type {@link com.secucard.connect.event.Events.AuthorizationFailed} if a STOMP auth.
   * problem or similar happens.
   * In both cases, or in general when a exception was thrown by this method, it's a good idea to call {@link #disconnect()}
   * to help clean up resources.<br/>
   * If the client is successfully connected an event of type {@link com.secucard.connect.event.Events.ConnectionStateChanged} is fired.
   */
  public synchronized void connect() {
    if (isConnected) {
      return;
    }
    try {
      getRestChannel().open(null); // init rest first since it does auth,
      context.getAuthProvider().getToken(); // fetch token
      Channel sc = getStompChannel();
      if (sc != null) {
        sc.open(null);
        startHeartBeat();
      }
      isConnected = true;
      context.getEventDispatcher().fireEvent(new Events.ConnectionStateChanged(true));
    } catch (RuntimeException e) {
      throw e;
    } catch (Throwable e) {
      throw new SecuException(e);
    }
  }

  public void cancelAuth() {
    getAuthProvider().cancelAuth();
  }

  public synchronized void disconnect() {
    try {
      Channel sc = getStompChannel();
      if (sc != null) {
        stopHeartBeat();
        sc.close(null);
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
   * Main service method for event processing. Takes JSON event data, processes them accordingly and returns the result
   * in a service callback hook method. The caller doesn't need to know anything about the provided event, all
   * handling is done internally. See the service for  specific event handling callback methods, prefixed with "on".<br/>
   * For processing of some events additional input beside the given event data is needed. In this cases a custom
   * {@link com.secucard.connect.event.AbstractEventHandler} implementation must be provided.
   *
   * @param json Contains the event data.
   * @return True if the event could be handled, false if no appropriate handler could be found and the event is not
   * handled.
   * @throws com.secucard.connect.SecuException if the given string provides no proper event data.
   */
  public synchronized boolean handleEvent(String json) {
    Event event;
    try {
      event = JsonMapper.get().mapEvent(json);
    } catch (Exception e) {
      throw new SecuException("Error processing event, invalid event data.", e);
    }

    return context.getEventDispatcher().handleEvent(event);
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

    // set up STOMP event listening
    Channel sc = getStompChannel();
    if (sc != null) {
      sc.setEventListener(new EventListener() {
        @Override
        public void onEvent(Object event) {
          handleEvent(event);
          context.getEventDispatcher().fireEvent(event);
        }
      });
    }

    // simply throws all exceptions by default, can be overwritten by clients user
    setExceptionHandler(new ThrowingExceptionHandler());
  }

  /**
   * Handle events before dispatching to listeners.
   */
  private void handleEvent(Object event) {
    // just log STOMP connection for now
    if (Events.STOMP_CONNECTED.equals(event)) {
      LOG.info("Connected to STOMP server.");
    } else if (Events.STOMP_DISCONNECTED.equals(event)) {
      LOG.info("Disconnected from STOMP server.");
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
