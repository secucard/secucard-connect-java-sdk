/*
 * Copyright (c) 2015. hp.weber GmbH & Co secucard KG (www.secucard.com)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.secucard.connect.product.general;

import com.secucard.connect.client.ProductService;
import com.secucard.connect.event.AbstractEventListener;
import com.secucard.connect.event.Events;
import com.secucard.connect.product.general.model.AccountDevice;
import com.secucard.connect.product.general.model.Event;

/**
 * Implements the general/account devices operations.
 */

public class AccountDevicesService extends ProductService<AccountDevice> {

  public static final ServiceMetaData<AccountDevice> META_DATA = new ServiceMetaData<>("general", "accountdevices", AccountDevice.class);

  @Override
  public ServiceMetaData<AccountDevice> getMetaData() {
    return META_DATA;
  }

  /**
   * Set a callback to get notified when a device has changed.
   */
  public void onAccountDevicesChanged(AccountDevicesListener listener) {
    context.eventDispatcher.registerListener(getMetaData().getObject() + Events.TYPE_CHANGED, listener);
  }

  public static abstract class AccountDevicesListener extends AbstractEventListener<AccountDevice> {

    @Override
    public boolean accept(Event<AccountDevice> event) {
      return Events.TYPE_CHANGED.equals(event.getType()) && META_DATA.getObject().equals(event.getTarget());
    }
  }
}
