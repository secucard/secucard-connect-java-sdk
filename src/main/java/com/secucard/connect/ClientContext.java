package com.secucard.connect;

import android.content.Context;
import android.provider.Settings;
import com.secucard.connect.auth.AuthProvider;
import com.secucard.connect.channel.Channel;
import com.secucard.connect.channel.rest.OAuthProvider;
import com.secucard.connect.channel.rest.RestChannel;
import com.secucard.connect.channel.rest.RestChannelBase;
import com.secucard.connect.channel.rest.VolleyChannel;
import com.secucard.connect.channel.stomp.StompChannel;
import com.secucard.connect.event.EventDispatcher;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.storage.AndroidStorage;
import com.secucard.connect.storage.DataStorage;
import com.secucard.connect.storage.DiskCache;
import com.secucard.connect.storage.MemoryDataStorage;
import com.secucard.connect.util.ResourceDownloader;
import com.secucard.connect.util.ThreadLocalUtil;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

  public Channel getRestChannel() {
    return restChannel;
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

  public Channel getStompChannel() {
    if (config.isStompEnabled()) {
      return stompChannel;
    } else {
      return null;
    }
  }

  public Channel getChannel(String name) {
    if (name.equals(STOMP)) {
      return stompChannel;
    }

    if (name.equals(REST)) {
      return restChannel;
    }
    return null;
  }

  public Channel getChannel() {
    String name = config.getDefaultChannel();
    if (STOMP.equals(name) && !config.isStompEnabled()) {
      return null; // should not happen
    }
    return getChannel(name);
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

      authProvider = new OAuthProvider(clientId, config) {
        @Override
        protected String getDeviceId() {
          String deviceId = super.getDeviceId();
          if (deviceId == null) {
            deviceId = Settings.Secure.getString(appContext.getContentResolver(), Settings.Secure.ANDROID_ID);
          }
          return deviceId;
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
      authProvider = new OAuthProvider(clientId, config);
    }

    this.dataStorage = dataStorage;

    authProvider.setDataStorage(dataStorage);
    authProvider.setRestChannel(restChannel);
    this.authProvider = authProvider;

    restChannel.setAuthProvider(this.authProvider);
    this.restChannel = restChannel;

    ResourceDownloader downloader = new ResourceDownloader();
    downloader.setCache(dataStorage);
    downloader.setHttpClient(restChannel);
    this.resourceDownloader = downloader;

    StompChannel sc = new StompChannel(clientId, config.getStompConfiguration());
    sc.setAuthProvider(this.authProvider);
    this.stompChannel = sc;

    this.clientId = clientId;
    this.config = config;
    this.runtimeContext = runtimeContext;

    this.eventDispatcher = new EventDispatcher();
  }
}

