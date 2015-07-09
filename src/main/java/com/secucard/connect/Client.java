package com.secucard.connect;

import com.secucard.connect.auth.*;
import com.secucard.connect.channel.JsonMapper;
import com.secucard.connect.channel.stomp.StompChannel;
import com.secucard.connect.channel.stomp.StompEvents;
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
  private TimerTask disconnectTimerTask;
  private static final Log LOG = new Log(Client.class);

  private Client(final String id, ClientConfiguration configuration, Object runtimeContext, DataStorage storage) {
    if (configuration == null) {
      throw new IllegalArgumentException("Configuration must not be null.");
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
  }

  /**
   * Register a single generic listener to get notified on multiple events:<br/>
   * - {@link #onConnectionStateChanged(com.secucard.connect.event.EventListener)}<br/>
   * - {@link #onServerError(com.secucard.connect.event.EventListener)}<br/>
   * - {@link #onAuthEvent(com.secucard.connect.event.EventListener)} <br/>
   * - {@link #onAuthorizationFailed(com.secucard.connect.event.EventListener)} <br/>
   * <p/>
   * You can use both (single or multiple variants) - but only one listener is allowed per event and so the last
   * invocation of the method always overwrites earlier ones.
   *
   * @param listener The listener instance. Pass null to unregister.
   */
  @SuppressWarnings("unchecked")
  public void onEvent(final EventListener listener) {
    onConnectionStateChanged(listener);
    onAuthorizationFailed(listener);
    onAuthEvent(listener);
    onServerError(listener);
  }

  /**
   * Get notified when {@link Events.ConnectionStateChanged} happens.
   * This event reflects the current overall connection state directly - if a connection goes on/off frequently in a
   * unstable network so also this state. It's up to the user to  "dampen".
   * <p/>
   * Useful to give indication in unstable networks if operation scan be performed or not.
   * Do NOT close the client based on this event.
   *
   * @see #onEvent(com.secucard.connect.event.EventListener)
   */
  public void onConnectionStateChanged(EventListener<Events.ConnectionStateChanged> listener) {
    getEventDispatcher().registerListener(Events.ConnectionStateChanged.class, listener);
  }

  /**
   * Get notified when {@link StompEvents.Error} happens.
   * Note: This indicates an unexpected and unrecoverable error condition, so this client is closed automatically when
   * config property "stomp.disconnectOnError" is set to true (default).
   * Event is usually only for informative purposes, all the important client stuff is already done.
   *
   * @see #onEvent(com.secucard.connect.event.EventListener)
   */
  public void onServerError(EventListener<StompEvents.Error> listener) {
    getEventDispatcher().registerListener(StompEvents.Error.class, listener);
  }

  /**
   * Get notified when {@link StompEvents.AuthorizationFailed} happens.
   * Note: Should usually not happen since all auth issues are signaled by throwing exceptions when connecting
   * this client! This indicates an unexpected and unrecoverable error condition, so this client is closed automatically
   * when config property "stomp.disconnectOnError" is set to true (default).
   * Event is usually only for informative purposes, all the important client stuff is already done.
   *
   * @see #onEvent(com.secucard.connect.event.EventListener)
   */
  public void onAuthorizationFailed(EventListener<StompEvents.AuthorizationFailed> listener) {
    getEventDispatcher().registerListener(StompEvents.AuthorizationFailed.class, listener);
  }

  /**
   * Get notified when one of {@link AuthProvider.Events} happens.
   *
   * @see #onEvent(com.secucard.connect.event.EventListener)
   */
  public void onAuthEvent(EventListener listener) {
    getAuthProvider().registerListener(listener);
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
   * The returned instance offers several business operations.
   * All returned services operate on the same resources of the client.
   * If a service method throws an exception or, in case of callback usage, the callback fails it is
   * recommended to call {@link #disconnect()} to close all connections are release all resources.
   *
   * @param serviceClass The actual service type.
   * @param <T>          The service type.
   * @return The service instance or null if not found.
   * @see #setServiceExceptionHandler(ExceptionHandler) how to setup central excepetion handling.
   */
  public <T extends AbstractService> T getService(Class<T> serviceClass) {
    return serviceFactory.getService(serviceClass);
  }


  public <T extends AbstractService> T getService(String serviceId) {
    return serviceFactory.getService(serviceId);
  }

  /**
   * Returns this client unique ID.
   */
  public String getId() {
    return id;
  }

  /**
   * Like {@link #connect(com.secucard.connect.auth.OAuthCredentials, boolean, Callback)}
   * but uses AnonymousCredentials , forceAuth = false.
   */
  public void connectAnonymous(Callback<Void> callback) {
    connect(new AnonymousCredentials(), false, callback);
  }

  /**
   * Like {@link #connect(com.secucard.connect.auth.OAuthCredentials, boolean, Callback)}
   * but no credentials and uses forceAuth = false.
   */
  public void connect(Callback<Void> callback) {
    connect(null, false, callback);
  }

  /**
   * Like {@link #connect(com.secucard.connect.auth.OAuthCredentials, boolean, Callback)}
   * but uses no credentials, forceAuth = false and no callback.
   */
  public void connect() {
    connect(null, false, null);
  }

  /**
   * Like {@link #connect(com.secucard.connect.auth.OAuthCredentials, boolean, Callback)} but uses forceAuth = false.
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
   * @throws AuthException   If the authentication failed for some reason, check exception details. May be an instance of
   *                         AuthCanceledException if the authentication was canceled by {@link #cancelAuth()}.
   * @throws ClientException If an unexpected error happens.
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
      throw new ClientException("Error while authenticating the credentials.", t);
    }

    // STOMP not allowed in anonymous mode
    if (context.config.isStompEnabled() && !(credentials instanceof AnonymousCredentials)) {
      Throwable throwable = ((StompChannel) getStompChannel()).startSessionRefresh();
      if (throwable != null) {
        disconnect(true);
        if (throwable instanceof AuthException) {
          clearAuthentication();
          throw (AuthException) throwable;
        }
        throw new ClientException("Error executing session refresh.", throwable);
      }
    }

    isConnected = true;

    getEventDispatcher().dispatch(new Events.ConnectionStateChanged(true), false);
  }

  public void disconnect() {
    disconnect(false);
  }

  private synchronized void disconnect(boolean force) {
    if (!force && !isConnected) {
      return;
    }
    LOG.info("Disconnect client.");

    if (disconnectTimerTask != null) {
      disconnectTimerTask.cancel();
      disconnectTimerTask = null;
    }

    isConnected = false;

    try {
      getRestChannel().close();
    } catch (Throwable e) {
      LOG.error("Error closing channel.", e);
    }
    if (context.config.isStompEnabled()) {
      try {
        getStompChannel().close();
      } catch (Throwable e) {
        LOG.error("Error closing channel.", e);
      }
    }

    getEventDispatcher().dispatch(new Events.ConnectionStateChanged(false), false);
    isConnected = false;
  }


  /**
   * Disconnects all client connections after a given time and closes all resources.
   * This can be useful to allow the client to listen for incoming events only for a certain period of time.
   *
   * @param seconds The time after this client should be closed.
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

  /**
   * Remove any authentication data from clients cache.
   * After that a new authentication for any given credentials is performed.
   */
  public void clearAuthentication() {
    context.getAuthProvider().clearToken();
  }

  /**
   * Gives an indication if this client is connected or not.
   *
   * @see #onConnectionStateChanged(com.secucard.connect.event.EventListener)
   */
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
   * @throws ClientException if the given JSON contains no valid event data.
   */
  public synchronized boolean handleEvent(String json, boolean async) {
    Event event;
    try {
      event = JsonMapper.get().map(json, Event.class);
    } catch (Exception e) {
      throw new ClientException("Error processing event, invalid event data.", e);
    }

    return getEventDispatcher().dispatch(event, async);
  }

  /**
   * Set an handler which receives all exceptions thrown by any service method or if a service callback is used the
   * exception the callback would receive. The {@link com.secucard.connect.Callback#failed(Throwable)} is NOT called in
   * the latter case.
   * By default no handler is set, that means all exceptions are thrown or returned by the callback.
   * <p/>
   * Note: With an
   *
   * @param exceptionHandler The handler to set.
   */
  public void setServiceExceptionHandler(ExceptionHandler exceptionHandler) {
    context.setExceptionHandler(exceptionHandler);
  }

  /**
   * Handle events from channels.
   * Dispatches to registered service event listeners to handle business related events
   * after filtering out and handling technical events.
   */
  private void handleChannelEvents(Object event) {
    Class<?> eventClass = event.getClass();
    // just log STOMP connection for now
    if (StompEvents.STOMP_CONNECTED.equals(event)) {
      getEventDispatcher().dispatch(new Events.ConnectionStateChanged(true), false);
    } else if (StompEvents.STOMP_DISCONNECTED.equals(event)) {
      getEventDispatcher().dispatch(new Events.ConnectionStateChanged(false), false);
    } else {
      if (StompEvents.Error.class.equals(eventClass) || StompEvents.AuthorizationFailed.class.equals(eventClass)) {
        if (context.getConfig().getStompConfiguration().isDisconnectOnError()) {
          // since this means a unexpected error and the stomp connection is closed anyway it makes no sense
          // to stay online
          disconnect();
        }
      } else {
        getEventDispatcher().dispatch(event, false);
      }
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
      throw new ClientException("No client credentials found in configuration.");
    }

    if ("device".equalsIgnoreCase(context.config.getAuthType())) {
      if (context.deviceId == null) {
        throw new ClientException("No credentials found in configuration");
      }
      credentials = new DeviceCredentials(credentials.getClientId(), credentials.getClientSecret(),
          context.getDeviceId());
    }
    return credentials;
  }

  /**
   * Timer task to perform the automatic disconnect.
   */
  private class DisconnectTimerTask extends TimerTask {
    @Override
    public void run() {
      LOG.info("Auto disconnect client.");
      Client.this.disconnect();
    }
  }
}
