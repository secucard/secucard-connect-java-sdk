package com.secucard.connect.service.general;

import android.util.Log;
import com.secucard.connect.Callback;
import com.secucard.connect.event.EventHandler;
import com.secucard.connect.event.Events;
import com.secucard.connect.model.general.AccountDevice;
import com.secucard.connect.model.general.Event;
import com.secucard.connect.service.AbstractService;

public class AccountDevicesService extends AbstractService {
  public static final String ID = AccountDevice.OBJECT + Events.TYPE_CHANGED;

  public void onAccountDevicesChanged(final Callback<Event> callback) {
    addOrRemoveEventHandler(ID, new AccountDevicesEventEventHandler(callback), callback);
  }

  private class AccountDevicesEventEventHandler extends EventHandler<Event, Event> {
    public AccountDevicesEventEventHandler(Callback<Event> callback) {
      super(callback);
    }

    @Override
    public boolean accept(Event event) {
      Log.d("AccountDevicesService", "Event: " + event.toString());
      return Events.TYPE_CHANGED.equals(event.getType()) && AccountDevice.OBJECT.equals(event.getTarget());
    }

    @Override
    public void handle(Event event) {
      completed(event);
    }
  }
}
