package com.secucard.connect;

import com.secucard.connect.auth.OAuthCredentials;
import com.secucard.connect.channel.Channel;
import com.secucard.connect.channel.ExecutionListener;
import com.secucard.connect.channel.JsonMapper;
import com.secucard.connect.channel.stomp.StompChannel;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.event.Events;
import com.secucard.connect.model.auth.Session;
import com.secucard.connect.model.general.Event;
import com.secucard.connect.model.transport.Result;
import com.secucard.connect.service.AbstractService;
import com.secucard.connect.service.ServiceFactory;
import com.secucard.connect.storage.DataStorage;
import com.secucard.connect.util.Log;
import com.secucard.connect.util.ThreadSleep;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Main entry to the Java Secucard Connect API.
 */
public class Client extends AbstractService {
  protected volatile boolean isConnected;
  private Thread heartbeatInvoker;
  private String id;
  private ServiceFactory serviceFactory;
  private boolean stopHeartbeat;
  private final Timer disconnectTimer;
  private TimerTask disconnectTimerTask;
  private KeepAliveLoop keepAliveLoop;

  private static final Log LOG = new Log(Client.class);

  private Client(final String id, ClientConfiguration configuration, Object runtimeContext, DataStorage storage) {
    if (configuration == null) {
      throw new SecuException("Configuration  must not be null.");
    }

    this.disconnectTimer = new Timer(true); // daemon thread needed
    this.keepAliveLoop = new Client.KeepAliveLoop();

    this.id = id;

    context = ClientContextFactory.create(id, configuration, runtimeContext, storage);

    serviceFactory = new ServiceFactory(context);
    isConnected = false;

    // set up STOMP event listening
    Channel sc = getStompChannel();
    if (sc != null) {
      sc.setEventListener(new EventListener() {
        @Override
        public void onEvent(Object event) {
          handleEvent(event);

          // todo: merge both
          if (event instanceof Event) {
            context.getEventDispatcher().handleEvent((Event) event);
          } else {
            context.getEventDispatcher().fireEvent(event);
          }
        }
      });
    }

    // simply throws all exceptions by default, can be overwritten by clients user
    setExceptionHandler(new ThrowingExceptionHandler());
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
   * Disconnects all client connections after a given time and closed all resources.
   *
   * @param seconds The time after all connections should be closed.
   * @return This client instance for method chaining.
   */
  public Client autoDisconnect(int seconds) {
    if (disconnectTimerTask != null) {
      disconnectTimerTask.cancel();
    }
    disconnectTimerTask = new DisconnectTimerTask();
    disconnectTimer.schedule(disconnectTimerTask, seconds * 1000);
    return this;
  }


  /**
   * Connect to secucard server anonymously.
   *
   * @return This client instance, allowing fluent interface.
   */
  public Client connect() {
    return connect(null, false);
  }


  /**
   * Connect to secucard server.
   *
   * @param credentials The credentials to use. Pass null to connect anonymously.
   * @param forceAuth   If true new authentication is forced using the credentials. If false client may use cached
   *                    authentication token from previous attempts for this credential if it is still valid,
   *                    avoiding a new authentication.
   *                    Falls back to true if no authentication token is available.
   * @return This client instance, allowing fluent interface.
   */
  public synchronized Client connect(OAuthCredentials credentials, boolean forceAuth) {
    if (disconnectTimerTask != null) {
      disconnectTimerTask.cancel();
    }

    ClientConfiguration configuration = context.getConfig();

    if (isConnected) {
      return this;
    }

    if (forceAuth) {
      getAuthProvider().clearCache();
    }

    getAuthProvider().setCredentials(credentials);

    if (credentials != null) {
      // not anonymous, so check if provided or cached credentials are valid
      try {
        getAuthProvider().authenticate();
      } catch (Throwable e) {
        doDisconnect();
        throw e;
      }
    }


    if (!configuration.isStompEnabled()) {
      // if stomp not enabled or no auth. - no keep alive, sop here
      isConnected = true;
//      connCallback.notify(true);
      return this;
    }


    isConnected = true;

    try {
      getStompChannel().open();
    } catch (Throwable t) {
      doDisconnect();
      throw new SecuException(t);
    }

    keepAliveLoop.await();

    if (keepAliveLoop.exception == null) {
//      connCallback.notify(true);
      return this;
    } else {
      doDisconnect();
      throw new SecuException(keepAliveLoop.exception);
    }
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
  public synchronized void connect_() {
    if (isConnected) {
      return;
    }
    try {
      getRestChannel().open(); // init rest first since it does auth,
      context.getAuthProvider().getToken(); // fetch token
      Channel sc = getStompChannel();
      if (sc != null) {
        sc.open();
        startHeartBeat();
        Thread.sleep(500);
      }
      isConnected = true;
      context.getEventDispatcher().fireEvent(new Events.ConnectionStateChanged(true));
    } catch (RuntimeException e) {
      throw e;
    } catch (InterruptedException e) {
      //ignore
    } catch (Throwable e) {
      throw new SecuException(e);
    }
  }

  /**
   * Cancel pending authentication process.
   */
  public void cancelAuth() {
    getAuthProvider().cancelAuth();
  }

  public void onConnectionStateChanged(Callback.Notify<Boolean> callback) {
//    connCallback = callback;
  }

  public void onAuthFailed(Callback.Notify<Object> callback) {
//    authFailedCallback = callback;
  }

  private void doDisconnect() {
    if (disconnectTimerTask != null) {
      disconnectTimerTask.cancel();
      disconnectTimerTask = null;
    }

    isConnected = false;

    if (keepAliveLoop != null && keepAliveLoop.isAlive()) {
      try {
        // wait until keep alive loop ended
        keepAliveLoop.join();
      } catch (InterruptedException e) {
        // ignore
      }
    }
    keepAliveLoop = null;

    try {
      getRestChannel().close();
    } catch (Throwable e) {
      LOG.error("error closing channel", e);
    }

    try {
      getStompChannel().close();
    } catch (Throwable e) {
      LOG.error("error closing channel", e);
    }

//    connCallback.notify(false);
  }

  public synchronized void disconnect() {
    try {
      Channel sc = getStompChannel();
      if (sc != null) {
        stopHeartBeat();
        try {
          sc.close();
        } catch (Exception e) {
          // ignore
        }
      }
      try {
        getRestChannel().close();
      } catch (Exception e) {
        // ignore
      }
      clear();
      // todo: clear data store?
    } finally {
      isConnected = false;
      context.getEventDispatcher().fireEvent(new Events.ConnectionStateChanged(false));
    }
  }

  /**
   * Remove any authentication data from clients cache.
   * After that a new authentication for any given credentials may be requested.
   */
  public void clearAuthentication() {
    context.getAuthProvider().clearCache();
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
    final int heartBeatMs = context.getConfig().getHeartBeatSec() * 1000;
    final int step = 500;
    if (context.getConfig().isStompEnabled()) {
      stopHeartBeat();
      stopHeartbeat = false;
      heartbeatInvoker = new Thread() {
        @Override
        public void run() {
          LOG.info("stomp heart beat started (", heartBeatMs, "s).");
          StompChannel channel = (StompChannel) getStompChannel();

          outer:
          while (true) {
            try {
              channel.execute(Session.class, "me", "refresh", null, null, Result.class, null);
            } catch (Throwable e) {
              try {
                channel.close();
              } catch (Exception e1) {
              }
              //ignore all just try to go on
            }

            for (int i = 0; i < heartBeatMs; i += step) {
              if (stopHeartbeat) {
                break outer;
              }
              try {
                Thread.sleep(step);
              } catch (InterruptedException e) {
                // ignore
              }
            }
          }

          LOG.info("stomp heart beat stopped");
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

  /**
   * Timer task to perform the automatic disconnect.
   */
  private class DisconnectTimerTask extends TimerTask {
    @Override
    public void run() {
      Client.this.disconnect();
    }
  }

  /**
   * Sends a confirmations within an fixed interval that this client is alive.
   * But is able to skip confirmation if other already confirmed this (setting isConfirmed to true).
   */
  private class KeepAliveLoop extends Thread implements ExecutionListener {
    private CountDownLatch latch;
    public Throwable exception;
    public volatile boolean isConfirmed;


    private KeepAliveLoop() {
      setDaemon(true);
    }

    @Override
    public void executed(Channel channel) {
      isConfirmed = true;
    }

    public void await() {
      latch = new CountDownLatch(1);
      super.start();
      try {
        latch.await();
      } catch (InterruptedException e) {
      }
    }

    @Override
    public void run() {
      LOG.info("keep alive loop started");
      outer:
      while (isConnected) {
        try {
          getStompChannel().execute(Session.class, "me", "refresh", null, null, Result.class, null);
          isConfirmed = false;
          LOG.info("keep alive sent");
        } catch (Throwable t) {
          LOG.info("keep alive failed");
          if (latch.getCount() != 0) {
            // first invocation after connect, let client know something is going wrong
            exception = t;
            latch.countDown();
            break;
          }
        }
        latch.countDown();// let main thread go on

        int step = 100;
        int interval = 8000;
        int count = 0;

        new ThreadSleep() {
          @Override
          protected boolean reset() {
            if (isConfirmed) {
              isConfirmed = false;
              return true;
            }
            return false;
          }

          @Override
          protected boolean cancel() {
            return !isConnected;
          }
        }.execute(8000, 100, TimeUnit.MILLISECONDS);


        // wait for next interval
        // reset wait counter if anybody confirmed for us
        while (true) {
          if (!isConnected) {
            break outer;
          }
          try {
            Thread.sleep(step);
          } catch (InterruptedException e) {
            // ignore
          }

          if (isConfirmed) {
            count = 0;
            isConfirmed = false;
          }

          count += step;
          if (count >= interval) {
            break;
          }
        }
      }

      LOG.info("keep alive loop ended");
    }
  }
}
