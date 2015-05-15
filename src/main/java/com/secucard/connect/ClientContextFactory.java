package com.secucard.connect;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import com.secucard.connect.auth.OAuthProvider;
import com.secucard.connect.auth.android.AnroidOAuthProvider;
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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Creates the client context by instantiating all beans and wiring them up.
 */
public class ClientContextFactory {

  public static ClientContext create(String clientId, ClientConfiguration config, Object runtimeContext,
                                     DataStorage dataStorage) {

    ClientContext ctx = new ClientContext();

    OAuthProvider authProvider;
    RestChannelBase restChannel;

    ctx.deviceId = config.getDeviceId();

    if (config.isAndroidMode()) {

      // android application context
      final Context appContext = (Context) runtimeContext;

      if (dataStorage == null) {
        DiskCache diskCache = null;
        String path = appContext.getCacheDir().getPath() + File.separator + clientId;
        try {
          diskCache = new DiskCache(path);
        } catch (IOException e) {
          throw new IllegalStateException("Can't creating file storage: " + path, e);
        }

        dataStorage = new AndroidStorage(appContext.getSharedPreferences("secuconnect", Context.MODE_PRIVATE),
            diskCache);
      }

      restChannel = new VolleyChannel(clientId, appContext, config.getRestConfiguration());

      // special auth provider for android, gets some additional information
      Map<String, String> info = new HashMap<>();
      info.put("", Build.VERSION.CODENAME);

      authProvider = new AnroidOAuthProvider(clientId,
          new OAuthProvider.Configuration(config.getOauthUrl(), config.getAuthWaitTimeoutSec(), true, info));

      if (ctx.deviceId == null) {
        ctx.deviceId = Settings.Secure.getString(appContext.getContentResolver(), Settings.Secure.ANDROID_ID);
      }

    } else {
      if (dataStorage == null) {
        if (config.getCacheDir() == null) {
          dataStorage = new MemoryDataStorage();
        } else {
          try {
            dataStorage = new DiskCache(config.getCacheDir() + File.separator + clientId);
          } catch (IOException e) {
            throw new IllegalStateException("Can't creating file storage: " + config.getCacheDir(), e);
          }
        }
      }

      restChannel = new RestChannel(clientId, config.getRestConfiguration());
      authProvider = new OAuthProvider(clientId,
          new OAuthProvider.Configuration(config.getOauthUrl(), config.getAuthWaitTimeoutSec(), true, null));
    }

    ctx.dataStorage = dataStorage;

    authProvider.setDataStorage(dataStorage);
    authProvider.setRestChannel(restChannel);
    ctx.authProvider = authProvider;

    restChannel.setAuthProvider(ctx.authProvider);
    ctx.restChannel = restChannel;

    ResourceDownloader downloader = ResourceDownloader.get();
    downloader.setCache(dataStorage);
    downloader.setHttpClient(restChannel);
    ctx.resourceDownloader = downloader;

    StompChannel sc = null;
    if (config.isStompEnabled()) {
      sc = new StompChannel(clientId, config.getStompConfiguration());
      sc.setAuthProvider(ctx.authProvider);
    }
    ctx.stompChannel = sc;

    ctx.clientId = clientId;
    ctx.config = config;
    ctx.runtimeContext = runtimeContext;

    ctx.eventDispatcher = new EventDispatcher();
    return ctx;
  }

}
