package com.secucard.connect.service;

import android.content.Context;
import com.secucard.connect.ClientContext;
import com.secucard.connect.channel.rest.VolleyChannel;
import com.secucard.connect.storage.AndroidStorage;
import com.secucard.connect.storage.DataStorage;

public class AndroidServiceFactory extends ServiceFactory {

  protected void setUpContext(ClientContext context) {

    // android application context
    Context runtimeContext = (Context) context.getRuntimeContext();

    // general storage
    DataStorage dataStorage = new AndroidStorage(runtimeContext.getSharedPreferences("secuconnect",
        Context.MODE_PRIVATE));

    // dedicated storage for auth, not sure if necessary
    AndroidStorage authStorage = new AndroidStorage(runtimeContext.getSharedPreferences("secuconnect.auth",
        Context.MODE_PRIVATE));

    context.setDataStorage(dataStorage);

    VolleyChannel vc = new VolleyChannel(context.getClientId(), runtimeContext, context.getConfig().getRestConfiguration());
    vc.setStorage(authStorage);
    context.setRestChannel(vc);

    setUpStomp(context, vc);
  }

}
