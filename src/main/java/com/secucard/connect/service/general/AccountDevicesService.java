package com.secucard.connect.service.general;

import com.secucard.connect.event.AbstractEventListener;
import com.secucard.connect.event.Events;
import com.secucard.connect.model.general.AccountDevice;
import com.secucard.connect.model.general.Event;
import com.secucard.connect.service.AbstractService;

public class AccountDevicesService extends AbstractService {

  public void onAccountDevicesChanged(AccountDevicesListener listener) {
    context.getEventDispatcher().registerListener(AccountDevice.OBJECT + Events.TYPE_CHANGED, listener);
  }

  public static abstract class AccountDevicesListener extends AbstractEventListener<Event<AccountDevice>> {
    @Override
    public boolean accept(Event<AccountDevice> event) {
      return Events.TYPE_CHANGED.equals(event.getType()) && AccountDevice.OBJECT.equals(event.getTarget());
    }
  }

}
