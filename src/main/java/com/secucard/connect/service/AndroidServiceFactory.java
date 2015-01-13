package com.secucard.connect.service;

import android.content.Context;
import android.provider.Settings;
import com.secucard.connect.ClientContext;
import com.secucard.connect.channel.rest.OAuthProvider;
import com.secucard.connect.channel.rest.VolleyChannel;
import com.secucard.connect.storage.AndroidStorage;
import com.secucard.connect.storage.DataStorage;

public class AndroidServiceFactory extends ServiceFactory {

  protected void setUpContext(final ClientContext context) {

    // android application context
    final Context runtimeContext = (Context) context.getRuntimeContext();

    // general storage
    DataStorage dataStorage = new AndroidStorage(runtimeContext.getSharedPreferences("secuconnect",
        Context.MODE_PRIVATE));

    // dedicated storage for auth, not sure if necessary
    AndroidStorage authStorage = new AndroidStorage(runtimeContext.getSharedPreferences("secuconnect.auth",
        Context.MODE_PRIVATE));

    context.setDataStorage(dataStorage);

    VolleyChannel vc = new VolleyChannel(context.getClientId(), runtimeContext, context.getConfig().getRestConfiguration());
    context.setRestChannel(vc);

    OAuthProvider ap = new OAuthProvider(context.getClientId(), context.getConfig()) {
      @Override
      protected String getDeviceId() {
        String deviceId = super.getDeviceId();
        if (deviceId == null) {
          deviceId = Settings.Secure.getString(runtimeContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return deviceId;
      }
    };
    ap.setDataStorage(dataStorage);
    ap.setRestChannel(vc);

    vc.setAuthProvider(ap);
    context.setAuthProvider(ap);

    setUpStomp(context, ap);
  }

}
