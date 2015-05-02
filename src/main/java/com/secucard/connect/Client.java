package com.secucard.connect;

import com.secucard.connect.auth.*;
import com.secucard.connect.channel.JsonMapper;
import com.secucard.connect.channel.stomp.StompChannel;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.event.Events;
import com.secucard.connect.model.general.Event;
import com.secucard.connect.service.AbstractService;
import com.secucard.connect.service.ServiceFactory;
import com.secucard.connect.storage.DataStorage;
import com.secucard.connect.util.Execution;
import com.secucard.connect.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Main entry to the Java Secucard Connect API.
 */
public class Client extends AbstractService {
  protected volatile boolean isConnected;
  private String id;
  private ServiceFactory serviceFactory;
  private final Timer disconnectTimer;
  private Callback.Notify<Boolean> connCallback;
  private TimerTask disconnectTimerTask;
  private static final Log LOG = new Log(Client.class);
  private EventListener authEventListener;

  private Client(final String id, ClientConfiguration configuration, Object runtimeContext, DataStorage storage) {
    if (configuration == null) {
      throw new SecuException("Configuration  must not be null.");
    }

    this.disconnectTimer = new Timer(true); // daemon thread needed

    this.id = id;

    context = ClientContextFactory.create(id, configuration, runtimeContext, storage);

    serviceFactory = new ServiceFactory(context);
    isConnected = false;

    // set up channel event listening
    // REST based channels don't support events
    if (getStompChannel() != null) {
      getStompChannel().setEventListener(new EventListener<Object>() {
        @Override
        public void onEvent(Object event) {
          handleChannelEvents(event);
        }
      });
    }

    // simply throws all exceptions by default, can be overwritten by clients user
    setExceptionHandler(new ThrowingExceptionHandler());
  }


  /**
   * Register a listener which gets notified about authentication related events.
   * 3 different event objects will delivered, all related to the device authorization process which
   * is performed in multiple steps:<br/>
   * {@link com.secucard.connect.auth.AuthProvider#EVENT_AUTH_PENDING} <br/>
   * {@link com.secucard.connect.auth.AuthProvider#EVENT_AUTH_OK}<br/>
   * {@link com.secucard.connect.model.auth.DeviceAuthCode}
   * <p/>
   * So registering makes not sense if this auth type is not used.
   *
   * @param listener The listener instance. Pass null to unregister.
   */
  public void onAuthenticationEven(EventListener listener) {
    getAuthProvider().registerEventListener(listener);
  }

  /**
   * Creating the client using no runtime context and storage.
   *
   * @see #Client(String, ClientConfiguration, Object, com.secucard.connect.storage.DataStorage)
   */
  public static Client create(String id, ClientConfiguration configuration) {
    return new Client(id, configuration, null, null);
  }

  public static Client create(String id, String path) throws IOException {
    return new Client(id, ClientConfiguration.fromProperties(path), null, null);
  }

  public static Client create(String id, InputStream inputStream) throws IOException {
    return new Client(id, ClientConfiguration.fromStream(inputStream), null, null);
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
   * Connect to secucard server anonymously.
   */
  public void connectAnonymous(Callback<Void> callback) {
    connect(new AnonymousCredentials(), false, callback);
  }

  /**
   * Connect to secucard server using stored credential information from client configuration.
   */
  public void connect(Callback<Void> callback) {
    connect(null, false, callback);
  }

  /**
   * Like {@link #connect(com.secucard.connect.auth.OAuthCredentials, boolean, Callback)},
   * but passing null
   */
  public void connect() {
    connect(null, false, null);
  }

  /**
   * Connect to secucard using the given credentials.
   *
   * @param credentials The credentials to use. Pass null to connect anonymously.
   */
  public void connect(OAuthCredentials credentials, Callback<Void> callback) {
    connect(credentials, false, callback);
  }

  /**
   * Connect to secucard using the given credentials.
   * Returns immediately if a callback is provided, else blocks until completion.
   * The client can be considered as connected when this method succeeded either by callback or by returning.
   * Calling this method has no effect when the client is in connected state, call {@link #disconnect()} before.
   * All resources are closed properly if the method fails, no need to call disconnect in that case.
   * Any timeout set by {@link #autoDisconnect(int)}) is cleared when this method is called.
   *
   * @param credentials The credentials to use. Pass null to use credentials from config.
   * @param forceAuth   If true new authentication is forced using the credentials. If false client may use cached
   *                    authentication token from previous attempts for this credential if it is still valid,
   *                    avoiding a new authentication.
   *                    Falls back to true if no authentication token is available.
   * @param callback    Callback to get notified when the connection attempt succeeded or failed. See "Throws" section
   *                    to get details about the exceptions passed on failure.
   * @throws com.secucard.connect.auth.AuthException         If the authentication failed for some reason,
   *                                                         check exception details.
   * @throws com.secucard.connect.auth.AuthCanceledException If the authentication was canceled by request.
   */
  public void connect(final OAuthCredentials credentials, final boolean forceAuth, Callback<Void> callback) {
    new Execution<Void>() {
      @Override
      protected Void execute() {
        connect(credentials, forceAuth);
        return null;
      }
    }.start(callback);
  }

  private synchronized void connect(OAuthCredentials credentials, boolean forceAuth) {
    if (isConnected) {
      return;
    }

    if (disconnectTimerTask != null) {
      disconnectTimerTask.cancel();
    }


    if (forceAuth) {
      getAuthProvider().clearToken();
    }

    if (credentials == null) {
      credentials = getCredentialsFromConfig();
    }

    getAuthProvider().setCredentials(credentials);

    try {
      // strict auth triggering only here on connect!
      getAuthProvider().getToken(true);
    } catch (Throwable t) {
      disconnect(true);
      if (t instanceof AuthException) {
        clearAuthentication();
        throw t;
      }
      throw new SecuException("Unknow error authenticating the credentials.", t);
    }

    // STOMP not allowed in anonymous mode
    if (context.config.isStompEnabled() && !(credentials instanceof AnonymousCredentials)) {
      Throwable throwable = ((StompChannel) getStompChannel()).startSessionRefresh();
      if (throwable != null) {
        disconnect(true);
        throw new SecuException("Error executing session refresh.", throwable);
      }
    }

    isConnected = true;

    if (connCallback != null) {
      connCallback.notify(true);
    }
  }

  public void disconnect() {
    disconnect(false);
  }

  private synchronized void disconnect(boolean force) {
    if (!force && !isConnected) {
      return;
    }

    if (disconnectTimerTask != null) {
      disconnectTimerTask.cancel();
      disconnectTimerTask = null;
    }

    isConnected = false;

    try {
      getRestChannel().close();
    } catch (Throwable e) {
      LOG.error("error closing channel", e);
    }

    if (context.config.isStompEnabled()) {
      try {
        getStompChannel().close();
      } catch (Throwable e) {
        LOG.error("error closing channel", e);
      }
    }

    if (connCallback != null) {
      connCallback.notify(false);
    }
    isConnected = false;
  }


  /**
   * Disconnects all client connections after a given time and closed all resources.
   *
   * @param seconds The time after all connections should be closed.
   */
  public synchronized void autoDisconnect(int seconds) {
    if (disconnectTimerTask != null) {
      disconnectTimerTask.cancel();
    }
    disconnectTimerTask = new DisconnectTimerTask();
    disconnectTimer.schedule(disconnectTimerTask, seconds * 1000);
  }

  /**
   * Cancel pending authentication process triggered by connect() call.
   * Must be called from another thread like connect().
   */
  public void cancelAuth() {
    getAuthProvider().cancelAuth();
  }

  public void onConnectionStateChanged(Callback.Notify<Boolean> callback) {
    connCallback = callback;
  }

  /**
   * Remove any authentication data from clients cache.
   * After that a new authentication for any given credentials is performed.
   */
  public void clearAuthentication() {
    context.getAuthProvider().clearToken();
  }

  public boolean isConnected() {
    return isConnected;
  }

  /**
   * Main service method for event processing. Takes JSON event data, processes them accordingly and returns the result
   * in a service callback hook method. The caller doesn't need to know anything about the provided event, all
   * handling is done internally. See the service for  specific event handling callback methods, prefixed with "on".<br/>
   * For processing of some events additional input beside the given event data is needed. In this cases a custom
   * {@link com.secucard.connect.event.EventListener} implementation must be provided.
   *
   * @param json  Contains the event data.
   * @param async If true the event processing by a handler is performed in a new thread causing this method to return
   *              immediately. False to handle in the main thread which will cause this method to block.
   * @return True if the event could be handled, false if no appropriate handler could be found and the event is not
   * handled.
   * @throws com.secucard.connect.SecuException if the given JSON contains no valid event data.
   */
  public synchronized boolean handleEvent(String json, boolean async) {
    Event event;
    try {
      event = JsonMapper.get().map(json, Event.class);
    } catch (Exception e) {
      throw new SecuException("Error processing event, invalid event data.", e);
    }

    return context.getEventDispatcher().dispatch(event, async);
  }

  public void setExceptionHandler(ExceptionHandler exceptionHandler) {
    context.setExceptionHandler(exceptionHandler);
  }

  /**
   * Handle events from channels.
   * Dispatches to registered service event listeners to handle business related events
   * after filtering out and handling technical events.
   */
  private void handleChannelEvents(Object event) {
    // just log STOMP connection for now
    if (Events.STOMP_CONNECTED.equals(event)) {
      LOG.info("Connected to STOMP server.");
    } else if (Events.STOMP_DISCONNECTED.equals(event)) {
      LOG.info("Disconnected from STOMP server.");
    } else {
      context.getEventDispatcher().dispatch(event, false);
    }
  }

  /**
   * Obtains credentials stored in configuration.
   *
   * @return Instances of ClientCredentials or DeviceCredentials, null if no credentials set up.
   */
  private OAuthCredentials getCredentialsFromConfig() {
    ClientCredentials credentials = context.config.getClientCredentials();
    if (credentials == null) {
      throw new AuthException("No client credentials found in configuration.");
    }

    if ("device".equalsIgnoreCase(context.config.getAuthType())) {
      if (context.deviceId == null) {
        throw new AuthException("No credentials found in configuration");
      }
      credentials = new DeviceCredentials(credentials.getClientId(), credentials.getClientSecret(),
          context.getDeviceId());
    }
    return credentials;
  }


  private static class ThrowingExceptionHandler implements ExceptionHandler {
    @Override
    public void handle(Throwable exception) {
      throw new SecuException(exception);
    }
  }

  /**
   * Timer task to perform the automatic disconnect.
   */
  private class DisconnectTimerTask extends TimerTask {
    @Override
    public void run() {
      Client.this.disconnect();
    }
  }


}
