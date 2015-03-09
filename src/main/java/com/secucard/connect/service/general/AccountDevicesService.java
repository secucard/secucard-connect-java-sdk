package com.secucard.connect.service.general;

import com.secucard.connect.Callback;
import com.secucard.connect.event.EventHandler;
import com.secucard.connect.model.general.AccountDevice;
import com.secucard.connect.model.general.Event;
import com.secucard.connect.service.AbstractService;

public class AccountDevicesService extends AbstractService {

  private static final String TYPE_CHANGED = "changed";

  public static final String ID = AccountDevice.OBJECT + TYPE_CHANGED;

  public void onAccountDevicesChanged(final Callback<AccountDevice> callback) {
    addOrRemoveEventHandler(ID, callback == null ? null : new AccountDevicesEventEventHandler(callback));
  }

  private class AccountDevicesEventEventHandler extends EventHandler<AccountDevice, Event> {
    public AccountDevicesEventEventHandler(Callback<AccountDevice> callback) {
      super(callback);
    }

    @Override
    public boolean accept(Event event) {
      return TYPE_CHANGED.equals(event.getType()) && AccountDevice.OBJECT.equals(event.getTarget());
    }

    @Override
    public void handle(Event event) {
      String e = event.getTarget();
    }
  }
}
