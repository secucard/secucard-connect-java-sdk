/*
 * Copyright (c) 2015. hp.weber GmbH & Co secucard KG (www.secucard.com)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.secucard.connect;

import com.secucard.connect.auth.AuthService;
import com.secucard.connect.auth.CancelCallback;
import com.secucard.connect.auth.ClientAuthDetails;
import com.secucard.connect.auth.TokenManager;
import com.secucard.connect.auth.model.AnonymousCredentials;
import com.secucard.connect.auth.model.ClientCredentials;
import com.secucard.connect.auth.model.OAuthCredentials;
import com.secucard.connect.auth.model.Token;
import com.secucard.connect.client.*;
import com.secucard.connect.event.EventDispatcher;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.event.Events;
import com.secucard.connect.net.Channel;
import com.secucard.connect.net.Options;
import com.secucard.connect.net.rest.JaxRsChannel;
import com.secucard.connect.net.rest.RestChannel;
import com.secucard.connect.net.rest.VolleyChannel;
import com.secucard.connect.net.stomp.StompChannel;
import com.secucard.connect.net.stomp.StompEvents;
import com.secucard.connect.net.util.JsonMapper;
import com.secucard.connect.product.common.model.SecuObject;
import com.secucard.connect.product.document.Document;
import com.secucard.connect.product.general.General;
import com.secucard.connect.product.general.model.Event;
import com.secucard.connect.product.loyalty.Loyalty;
import com.secucard.connect.product.payment.Payment;
import com.secucard.connect.product.services.Services;
import com.secucard.connect.product.smart.Smart;
import com.secucard.connect.util.ExceptionMapper;
import com.secucard.connect.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * The entry point to the secucard API, provides resources for product operations.
 */
public class SecucardConnect {
  public static final String VERSION = "1.0.0";

  protected volatile boolean isConnected;
  private Configuration configuration;
  private Timer disconnectTimer;
  private TimerTask disconnectTimerTask;
  private ClientContext context;
  private Map<Class<? extends ProductService>, ProductService<? extends SecuObject>> serviceMap = new HashMap<>();
  private AuthService authService;
  private static final Log LOG = new Log(SecucardConnect.class);

  /**
   * Get notified when one of {@link com.secucard.connect.auth.Events} happens.<br/>
   * These events may be fired only during the auth process triggered by calling {@link #open()}.
   * Never happening again after open() returns.
   */
  public void onAuthEvent(EventListener listener) {
    context.tokenManager.registerListener(listener);
  }

  /**
   * Get notified when {@link com.secucard.connect.event.Events.ConnectionStateChanged} happens.
   * Note: Connection state changes may be of temporary nature depending on the quality of the users network connection
   * for instance and the fact that this client library always tries to reconnect automatically.
   * If the state changes to not connected it can be assumed that subsequent secucard API calls will fail.
   * So this event may be used to indicate the a "health" state to the user, to preventing him accessing features which
   * are not available.
   * Do NOT close the client based on this event.
   */
  public void onConnectionStateChanged(EventListener<Events.ConnectionStateChanged> listener) {
    context.eventDispatcher.registerListener(Events.ConnectionStateChanged.class, listener);
  }

  /**
   * Set an handler which receives all exceptions thrown by any product method or if a callback is used the
   * exception the callback would receive. The {@link com.secucard.connect.client.Callback#failed(Throwable)} is NOT called in
   * the latter case.
   * By default no handler is set, that means all exceptions are thrown or returned by the callback.
   *
   * @param exceptionHandler The handler to set.
   */
  public void setServiceExceptionHandler(ExceptionHandler exceptionHandler) {
    context.exceptionHandler = exceptionHandler;
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
   *              immediately. Set to false to handle in the main thread which will cause this method to block.
   * @return True if the event could be handled, false if no appropriate handler could be found and the event is not
   * handled.
   * @throws com.secucard.connect.client.ClientError if the given JSON contains no valid event data.
   */
  public synchronized boolean handleEvent(String json, boolean async) {
    Event event;
    try {
      event = context.jsonMapper.map(json, Event.class);
    } catch (Exception e) {
      throw new ClientError("Error processing event, invalid event data.", e);
    }

    return context.eventDispatcher.dispatch(event, async);
  }

  /**
   * Opens this client resources.
   * Attempts also to validate the token provided by {@link SecucardConnect.Configuration#clientAuthDetails}. If not valid
   * or null the token is refreshed or obtained new. To do this credentials are requested by using the attached
   * {@link SecucardConnect.Configuration#clientAuthDetails}. Depending on the type of credentials an authentication
   * process may start also causing events which will be delivered by the EventListener set by
   * {@link #onAuthEvent(com.secucard.connect.event.EventListener)}.
   * <p/>
   * This method blocks execution of the current thread until finished. If the method fails all resources are
   * already released, no need to call {@link #close()}.
   *
   * @throws AuthError    If authentication problem exist, check the actual type like
   *                      {@link com.secucard.connect.auth.exception.AuthDeniedException} to get
   *                      further details about the problem and how to handle (like repeating)
   * @throws NetworkError if the network failed.
   * @throws ClientError  if any other non recoverable error happened.
   */
  public synchronized void open() throws AuthError, NetworkError, ClientError {
    if (isConnected) {
      return;
    }

    if (disconnectTimerTask != null) {
      disconnectTimerTask.cancel();
    }

    try {
      context.tokenManager.getToken(true);
    } catch (AuthError e) {
      close();
      throw e;
    }

    try {
      for (Channel channel : context.channels.values()) {
        channel.open();
      }
    } catch (Exception e) {
      close();
      throw ExceptionMapper.map(e, "Error opening secucard connect client.");
    }

    isConnected = true;

    context.eventDispatcher.dispatch(new Events.ConnectionStateChanged(true), false);
    LOG.debug("Secucard connect client opened.");
  }

  /**
   * Gracefully closes this instance and releases all resources.
   */
  public void close() {
    isConnected = false;
    try {
      for (Channel channel : context.channels.values()) {
        if (channel != null) {
          channel.close();
        }
      }
    } catch (Exception e) {
      LOG.error(e);
    }
    context.eventDispatcher.dispatch(new Events.ConnectionStateChanged(false), false);
    LOG.debug("Secucard connect client closed.");
  }

  /**
   * Disconnects all client connections after a given time and closes this client.
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
   * Obtain a service reference by type. Returns always the same instance for a given type.
   *
   * @param type The class of the service. Use the static fields provided by one of the product specific constant class
   *             existing in every product package like {@link com.secucard.connect.product.payment.Payment}
   *             instead of giving type directly.
   * @return The service instance.
   * @see com.secucard.connect.client.ProductService
   */
  @SuppressWarnings({"unchecked"})
  public <T> T service(Class<T> type) {
    if (type.equals(AuthService.class)) {
      return (T) authService;
    }
    return (T) serviceMap.get(type);
  }

  /**
   * Create a new SecuConnect instance.
   *
   * @param config The settings to configure the new instance. Pass null to use the default settings.
   * @return The new instance.
   * @throws ClientError if an error happens.
   */
  public static SecucardConnect create(Configuration config)
      throws ClientError {
    if (config == null) {
      config = Configuration.get();
    }

    if (config.dataStorage == null) {
      throw new ClientError("Missing cache implementation found in config.");
    }

    final SecucardConnect sc = new SecucardConnect();
    sc.configuration = config;

    ClientContext ctx = new ClientContext();
    sc.context = ctx;

    ctx.appId = config.appId;

    TokenManager.Configuration authCfg = new TokenManager.Configuration(config.properties, null);
    StompChannel.Configuration stompCfg = new StompChannel.Configuration(config.properties);
    RestChannel.Configuration restConfig = new RestChannel.Configuration(config.properties);

    LOG.info("Creating client with configuration: ", config, "; ", authCfg, "; ", stompCfg, "; ", restConfig);

    sc.disconnectTimer = new Timer(true); // daemon thread needed

    if (config.clientAuthDetails == null) {
      config.clientAuthDetails = new ClientAuthDetails() {
        @Override
        public OAuthCredentials getCredentials() {
          return new AnonymousCredentials();
        }

        @Override
        public ClientCredentials getClientCredentials() {
          return null;
        }

        @Override
        public Token getCurrent() {
          return null;
        }

        @Override
        public void onTokenChanged(Token token) {
        }
      };
    }

    // set up channels
    ctx.runtimeContext = config.runtimeContext;
    ctx.defaultChannel = config.defaultChannel;
    RestChannel rc;
    if (config.androidMode) {
      if (config.runtimeContext == null) {
        throw new ClientError("Missing Android application context.");
      }
      rc = new VolleyChannel(restConfig, ctx);
    } else {
      rc = new JaxRsChannel(restConfig, ctx);
    }
    Map<String, Channel> channels = new HashMap<>();
    channels.put(Options.CHANNEL_REST, rc);
    if (config.stompEnabled) {
      StompChannel channel = new StompChannel(stompCfg, ctx);
      channels.put(Options.CHANNEL_STOMP, channel);
    }
    for (Channel channel : channels.values()) {
      channel.setEventListener(new EventListener<Object>() {
        @Override
        public void onEvent(Object event) {
          sc.handleChannelEvents(event);
        }
      });
    }
    ctx.channels = channels;

    AuthService authService = new AuthService(rc, authCfg.oauthUrl);
    authService.setUserAgentInfo("secucardconnect-java-" + VERSION + "/java:" + System.getProperty("java.vendor")
        + " " + System.getProperty("java.version"));

    sc.authService = authService;

    ctx.tokenManager = new TokenManager(authCfg, config.clientAuthDetails, config.autCancelCallback,
        authService);

    // set up event dispatcher
    ctx.eventDispatcher = new EventDispatcher();

    // set up services
    Map<Class<? extends ProductService>, ProductService<? extends SecuObject>> services =
        ServiceFactory.createServices(ctx);
    sc.serviceMap.putAll(services);

    // set up JSON mapper instance
    ctx.jsonMapper = new JsonMapper();
    ctx.jsonMapper.init(services.values());

    sc.wireServiceInstances();

    return sc;
  }

  /**
   * Handle events from channels.
   * Dispatches to registered service event listeners to handle business related events
   * after filtering out and handling technical events.
   */
  private void handleChannelEvents(Object event) {
    if (StompEvents.STOMP_CONNECTED.equals(event)) {
      context.eventDispatcher.dispatch(new Events.ConnectionStateChanged(true), true);
    } else if (StompEvents.STOMP_DISCONNECTED.equals(event)) {
      context.eventDispatcher.dispatch(new Events.ConnectionStateChanged(false), true);
    } else {
      context.eventDispatcher.dispatch(event, true);
    }
  }

  /**
   * Timer task to perform the automatic disconnect.
   */
  private class DisconnectTimerTask extends TimerTask {
    @Override
    public void run() {
      LOG.info("Auto disconnect client.");
      SecucardConnect.this.close();
    }
  }

  // provide service instances for easy access ------------------------------------------------------------------------

  public Document document;
  public General general;
  public Payment payment;
  public Loyalty loyalty;
  public Services services;
  public Smart smart;


  private void wireServiceInstances() {

    document = new Document(service(Document.Uploads));

    general = new General(service(General.Accountdevices), service(General.Accounts), service(General.Merchants),
        service(General.News), service(General.Publicmerchants), service(General.Stores), service(General.Transactions));

    payment = new Payment(service(Payment.Containers), service(Payment.Customers), service(Payment.Secupaydebits),
        service(Payment.Secupayprepays), service(Payment.Contracts));

    loyalty = new Loyalty(service(Loyalty.Cards), service(Loyalty.Customers), service(Loyalty.Merchantcards));

    services = new Services(service(Services.Identrequests), service(Services.Identresults));

    smart = new Smart(service(Smart.Checkins), service(Smart.Idents), service(Smart.Transactions));
  }

  /**
   * Main configuration of the SDK client. Supports properties:
   * <p/>
   * Client:<br/>
   * - androidMode (false), set to true if use in Android environment, false else <br/>
   * - stompEnabled (true), set to true to enable usage of STOMP communication, false else <br/>
   * - defaultChannel (rest), the default server communication channel <br/>
   * - appId (null), the app id if used in a custom app <br/>
   * - cacheDir (".scc-cache"), the directory for the cache <br/>
   * - logging.local(false), set to true to enable local logging and ignoring global settings <br/>
   * - logging.pattern(scc.log), the logging file path <br/>
   * - logging.limit(1000000), the max log file size in b, 1mb <br/>
   * - logging.count(10), the max number of files to keep <br/>
   * - logging.level(INFO), the log level  <br/>
   * - logging.format("%1$tD %1$tH:%1$tM:%1$tS:%1$tL %4$s %2$s - %5$s %6$s%n"), output format  <br/>
   * <p/>
   * OAuth:<br/>
   * - see {@link com.secucard.connect.auth.TokenManager.Configuration}
   * <p/>
   * REST:<br/>
   * - see {@link RestChannel.Configuration}
   * <p/>
   * STOMP:<br/>
   * - see {@link StompChannel.Configuration}
   * <p/>
   * Additionally set custom instances for:<br/>
   * - {@link #dataStorage}<br/>
   * - {@link #runtimeContext}<br/>
   * - {@link #clientAuthDetails} <br/>
   * - {@link #autCancelCallback} <br/>
   */
  public static final class Configuration {
    private final Properties properties;
    private final String defaultChannel;
    private final String loggingConfig;
    private final boolean androidMode;
    private final boolean stompEnabled;
    private final String appId;
    private final String cacheDir;
    public final String logFormat;
    public final String logLevel;
    public final String logPattern;
    public final int logLimit;
    public final int logCount;
    public final boolean logIgnoreGlobal;

    /**
     * Set the property with given name. Can be used to change properties in programmatic way without config file.
     */
    public void property(String name, String value) {
      properties.setProperty(name, value);
    }

    /**
     * Get the property with the given name.
     */
    public String property(String name) {
      return properties.getProperty(name);
    }

    /**
     * Storage interface used as cache. Default is {@link com.secucard.connect.client.DiskCache} with path set
     * to {@link #cacheDir}.
     */
    public DataStorage dataStorage;

    /**
     * The ClientAuthDetails implementation to use. Default returns AnonymousCredentials and no tokens.
     */
    public ClientAuthDetails clientAuthDetails;

    /**
     * Callback instance who indicates if any pending authentication should be canceled.
     * Set null if no such a callback is needed. Default null.
     */
    public CancelCallback autCancelCallback;

    /**
     * Android application context object. Mandatory when property androidMode = true.  Default null.
     */
    public Object runtimeContext;

    /**
     * Returns the default configuration.
     *
     * @throws ClientError If a error occurs.
     */
    public static Configuration get() {
      return get("config.properties");
    }

    /**
     * Get config from file.
     *
     * @param path The file path. If this path is relative (no leading path separator) it will be treated as classpath relative path.
     * @throws ClientError If a error occurs.
     */
    public static Configuration get(String path) {
      File file = new File(path);
      InputStream inputStream;
      if (file.isAbsolute()) {
        try {
          inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
          throw new ClientError("Error loading configuration properties.", e);
        }
      } else {
        // relative path, treat as classpath relative
        inputStream = Configuration.class.getClassLoader().getResourceAsStream(path);
      }

      return get(inputStream);
    }

    /**
     * Get config from stream
     *
     * @throws ClientError If a error occurs.
     */
    public static Configuration get(InputStream inputStream) {
      try {
        Properties p = new Properties();
        p.load(inputStream);
        Configuration configuration = new Configuration(p);
        configuration.dataStorage = new DiskCache(configuration.cacheDir);
        return configuration;
      } catch (Exception e) {
        throw new ClientError("Error loading configuration properties.", e);
      }
    }

    Configuration(Properties properties) {
      this.properties = properties;
      defaultChannel = properties.getProperty("defaultChannel", Options.CHANNEL_REST).trim().toLowerCase();
      loggingConfig = properties.getProperty("loggingConfig");
      androidMode = Boolean.parseBoolean(properties.getProperty("androidMode", "false"));
      stompEnabled = Boolean.parseBoolean(properties.getProperty("stompEnabled", "true"));
      logIgnoreGlobal = Boolean.valueOf(properties.getProperty("logging.local", "false"));
      logCount = Integer.valueOf(properties.getProperty("logging.count", "10"));
      logLimit = Integer.valueOf(properties.getProperty("logging.limit", "1000000"));
      logPattern = properties.getProperty("logging.pattern", "scc.log");
      logLevel = properties.getProperty("logging.level", "INFO");
      logFormat = properties.getProperty("logging.format", "%1$tD %1$tH:%1$tM:%1$tS:%1$tL %4$s %2$s - %5$s %6$s%n");
      appId = properties.getProperty("appId");
      cacheDir = properties.getProperty("cacheDir", ".scc-cache");
    }


    @Override
    public String toString() {
      return "Configuration{" +
          "defaultChannel='" + defaultChannel + '\'' +
          ", loggingConfig='" + loggingConfig + '\'' +
          ", androidMode=" + androidMode +
          ", stompEnabled=" + stompEnabled +
          ", logFormat='" + logFormat + '\'' +
          ", logLevel='" + logLevel + '\'' +
          ", logPath='" + logPattern + '\'' +
          ", logLimit=" + logLimit +
          ", logCount=" + logCount +
          ", logIgnoreGlobal=" + logIgnoreGlobal +
          ", dataStorage=" + dataStorage +
          ", clientAuthDetails=" + clientAuthDetails +
          ", autCancelCallback=" + autCancelCallback +
          ", runtimeContext=" + runtimeContext +
          '}';
    }
  }
}
