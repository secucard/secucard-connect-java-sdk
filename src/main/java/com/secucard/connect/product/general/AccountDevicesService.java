package com.secucard.connect.product.general;

import com.secucard.connect.client.ProductService;
import com.secucard.connect.event.AbstractEventListener;
import com.secucard.connect.event.Events;
import com.secucard.connect.product.general.model.AccountDevice;
import com.secucard.connect.product.general.model.Event;

public class AccountDevicesService extends ProductService<AccountDevice> {

  @Override
  protected ServiceMetaData<AccountDevice> createMetaData() {
    return new ServiceMetaData<>("general", "accountdevices", AccountDevice.class);
  }

  public void onAccountDevicesChanged(AccountDevicesListener listener) {
    if (listener != null) {
      listener.service = this;
    }
    context.eventDispatcher.registerListener(getMetaData().getObject() + Events.TYPE_CHANGED, listener);
  }

  public static abstract class AccountDevicesListener extends AbstractEventListener<AccountDevice> {
    protected AccountDevicesService service;

    @Override
    public boolean accept(Event<AccountDevice> event) {
      return Events.TYPE_CHANGED.equals(event.getType()) && service.getMetaData().getObject().equals(event.getTarget());
    }

  }
}
