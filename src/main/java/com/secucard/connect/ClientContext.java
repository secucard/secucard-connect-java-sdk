package com.secucard.connect;

import android.content.Context;
import android.provider.Settings;
import com.secucard.connect.auth.AuthProvider;
import com.secucard.connect.auth.OAuthProvider;
import com.secucard.connect.channel.Channel;
import com.secucard.connect.channel.rest.RestChannel;
import com.secucard.connect.channel.rest.RestChannelBase;
import com.secucard.connect.channel.rest.VolleyChannel;
import com.secucard.connect.channel.stomp.StompChannel;
import com.secucard.connect.event.EventDispatcher;
import com.secucard.connect.storage.AndroidStorage;
import com.secucard.connect.storage.DataStorage;
import com.secucard.connect.storage.DiskCache;
import com.secucard.connect.storage.MemoryDataStorage;
import com.secucard.connect.util.ResourceDownloader;
import com.secucard.connect.util.ThreadLocalUtil;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Context instance holding all necessary beans used in client.
 */
public class ClientContext {
  public static final String STOMP = "stomp";
  public static final String REST = "rest";

  protected DataStorage dataStorage;
  protected Channel restChannel;
  protected Channel stompChannel;
  protected AuthProvider authProvider;
  protected ClientConfiguration config;
  protected String clientId;
  protected ExceptionHandler exceptionHandler;
  protected Object runtimeContext;
  protected ResourceDownloader resourceDownloader;
  protected EventDispatcher eventDispatcher;

  public ClientContext(String clientId, ClientConfiguration config, Object runtimeContext, DataStorage dataStorage) {
    init(clientId, config, runtimeContext, dataStorage);
  }

  /**
   * Obtain the current client context instance..
   */
  public static ClientContext get() {
    return (ClientContext) ThreadLocalUtil.get(ClientContext.class.getName());
  }

  public EventDispatcher getEventDispatcher() {
    return eventDispatcher;
  }

  public void setEventDispatcher(EventDispatcher eventDispatcher) {
    this.eventDispatcher = eventDispatcher;
  }

  public DataStorage getDataStorage() {
    return dataStorage;
  }

  public AuthProvider getAuthProvider() {
    return authProvider;
  }

  public ClientConfiguration getConfig() {
    return config;
  }

  public String getClientId() {
    return clientId;
  }

  public ExceptionHandler getExceptionHandler() {
    return exceptionHandler;
  }

  public void setExceptionHandler(ExceptionHandler exceptionHandler) {
    this.exceptionHandler = exceptionHandler;
  }

  public Object getRuntimeContext() {
    return runtimeContext;
  }

  public ResourceDownloader getResourceDownloader() {
    return resourceDownloader;
  }

  /**
   * Return a channel to the given name.
   *
   * @param name The channel name or null for default channel.
   *             Valid names are: {@link ClientContext#STOMP}, {@link ClientContext#REST}.
   * @return Null if the requested channel is not available or disabled by config, the channel instance else.
   * @throws java.lang.IllegalArgumentException if the name is not valid.
   */
  public Channel getChannel(String name) {
    if (name == null) {
      name = config.getDefaultChannel();
    }

    if (REST.equals(name)) {
      return restChannel;
    }

    if (STOMP.equals(name)) {
      return stompChannel;
    }

    throw new IllegalArgumentException("invalid channel name " + name);
  }

  /**
   * Initialize beans in this context and wiring up dependencies.
   * Checks for androidMode config property and does special initialization.
   */

  private void init(String clientId, ClientConfiguration config, Object runtimeContext, DataStorage dataStorage) {
    OAuthProvider authProvider;
    RestChannelBase restChannel;

    if (config.isAndroidMode()) {

      // android application context
      final Context appContext = (Context) runtimeContext;

      if (dataStorage == null) {
        DiskCache diskCache = null;
        String path = appContext.getCacheDir().getPath() + File.separator + clientId;
        try {
          diskCache = new DiskCache(path);
        } catch (IOException e) {
          throw new SecuException("Error creating file storage: " + path, e);
        }

        dataStorage = new AndroidStorage(appContext.getSharedPreferences("secuconnect", Context.MODE_PRIVATE),
            diskCache);
      }

      restChannel = new VolleyChannel(clientId, appContext, config.getRestConfiguration());

      // special auth provider for android, gets some additional information
      authProvider = new OAuthProvider(clientId,
          new OAuthProvider.Configuration(config.getOauthUrl(), config.getDeviceId(), config.getAuthWaitTimeoutSec(), true)) {
        @Override
        protected String getDeviceId() {
          String deviceId = super.getDeviceId();
          if (deviceId == null) {
            deviceId = Settings.Secure.getString(appContext.getContentResolver(), Settings.Secure.ANDROID_ID);
          }
          return deviceId;
        }

        @Override
        protected Map<String, String> getDeviceInfo() {
          return null;
//          Map<String, String> info = new HashMap<>();
//          info.put("", Build.VERSION.CODENAME);
//          return info;
        }
      };

    } else {
      if (dataStorage == null) {
        if (config.getCacheDir() == null) {
          dataStorage = new MemoryDataStorage();
        } else {
          try {
            dataStorage = new DiskCache(config.getCacheDir() + File.separator + clientId);
          } catch (IOException e) {
            throw new SecuException("Error creating file storage: " + config.getCacheDir(), e);
          }
        }
      }

      restChannel = new RestChannel(clientId, config.getRestConfiguration());
      authProvider = new OAuthProvider(clientId,
          new OAuthProvider.Configuration(config.getOauthUrl(), config.getDeviceId(), config.getAuthWaitTimeoutSec(), true));
    }

    this.dataStorage = dataStorage;

    authProvider.setDataStorage(dataStorage);
    authProvider.setRestChannel(restChannel);
    this.authProvider = authProvider;

    restChannel.setAuthProvider(this.authProvider);
    this.restChannel = restChannel;

    ResourceDownloader downloader = ResourceDownloader.get();
    downloader.setCache(dataStorage);
    downloader.setHttpClient(restChannel);
    this.resourceDownloader = downloader;

    StompChannel sc = null;
    if (config.isStompEnabled()) {
      sc = new StompChannel(clientId, config.getStompConfiguration());
      sc.setAuthProvider(this.authProvider);
    }
    this.stompChannel = sc;

    this.clientId = clientId;
    this.config = config;
    this.runtimeContext = runtimeContext;

    this.eventDispatcher = new EventDispatcher();
  }
}

