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
import com.secucard.connect.event.EventListener;
import com.secucard.connect.net.Options;
import com.secucard.connect.product.general.model.*;

import java.util.List;

public class AccountsService extends ProductService<Account> {

  public static final String EVENT_TYPE_BEACON_MONITOR = "BeaconMonitor";

  @Override
  public ServiceMetaData<Account> createMetaData() {
    return new ServiceMetaData<>("general", "accounts", Account.class);
  }

  @Override
  public Options getDefaultOptions() {
    return new Options(Options.CHANNEL_STOMP);
  }

  public void onBeaconMonitor(AccountEventListener listener) {
    if (listener != null) {
      listener.service = this;
    }
    context.eventDispatcher.registerListener(getMetaData().getObject() + EVENT_TYPE_BEACON_MONITOR, listener);
  }

  /**
   * Updates the location of a account.
   *
   * @param accountId The account to update.
   * @param location  The new geo location to set.
   * @return True if successfully updated, false else.
   */
  public boolean updateLocation(String accountId, Location location) {
    return super.updateToBool(accountId, "location", null, location, null, null);
  }

  /**
   * Updates the beacons of the account
   *
   * @param accountId  Account ID
   * @param beaconList List of beacons near by
   * @return True if successfully updated, false else.
   */
  public boolean updateBeacons(String accountId, List<BeaconEnvironment> beaconList) {
    return super.updateToBool("me", "beaconEnvironment", null, beaconList, null, null);
  }

  public boolean updateGCM(String accountId, Object objectArg) {
    return super.updateToBool(accountId, "gcm", null, objectArg, null, null);
  }


  /**
   * Set a listener when interested to get notified about merchants around a location.
   * To set a location use {@link #updateLocation(String, com.secucard.connect.product.general.model.Location)}.
   * Set to null to remove a listener.
   */
  public void onMerchantsChanged(EventListener<MerchantList> listener) {
    context.eventDispatcher.registerListener(MerchantList.class, listener);
  }

  public static abstract class AccountEventListener extends AbstractEventListener<Account> {
    protected AccountsService service;

    @Override
    public boolean accept(Event<Account> event) {
      return EVENT_TYPE_BEACON_MONITOR.equals(event.getType()) && service.getMetaData().getObject().equals(event.getTarget());
    }
  }

}
