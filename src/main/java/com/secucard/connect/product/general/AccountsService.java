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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implements the general/accounts operations.
 */

public class AccountsService extends ProductService<Account> {
  public static final String EVENT_TYPE_BEACON_MONITOR = "BeaconMonitor";
  public static final ServiceMetaData<Account> META_DATA = new ServiceMetaData<>("general", "accounts", Account.class);

  @Override
  public ServiceMetaData<Account> getMetaData() {
    return META_DATA;
  }

  /**
   * Create and return new account with the given data. Needs no authentication.
   *
   * @throws Exception If an error happens, see {@link ProductService} which ones.
   */
  @Override
  public Account create(Account object) {
    // overrides super to pass special option
    Options options = getDefaultOptions();
    options.anonymous = true;
    return super.create(object, options, null);
  }

  /**
   * Set a listener to get notified when a beacon was monitored.
   */
  public void onBeaconMonitor(AccountEventListener listener) {
    context.eventDispatcher.registerListener(getMetaData().getObject() + EVENT_TYPE_BEACON_MONITOR, listener);
  }

  /**
   * Updates the location of the current user account.
   *
   * @return True if successfully updated, false else.
   */
  public boolean updateLocation(Location location) {
    Options options = getDefaultOptions();
    options.channel = Options.CHANNEL_STOMP;
    return super.updateToBool("me", "location", null, location, options, null);
  }

  /**
   * Updates the current users account with the given beacons.
   *
   * @return True if successfully updated, false else.
   */
  public boolean updateBeacons(List<BeaconEnvironment> beaconList) {
    Options options = getDefaultOptions();
    options.channel = Options.CHANNEL_STOMP;
    return super.updateToBool("me", "beaconEnvironment", null, beaconList, options, null);
  }

  /**
   * Update the current users Google Cloud Messaging with the given registration id.
   *
   * @return True if successfully updated, false else.
   */
  public boolean updateGCM(String id) {
    Options options = getDefaultOptions();
    options.channel = Options.CHANNEL_STOMP;
    Map arg = new HashMap();
    arg.put("registrationId", id);
    return super.updateToBool("me", "gcm", null, arg, options, null);
  }

  /**
   * Send password reset link. Needs no authentication.
   *
   * @param email  The email account to send the link to.
   * @param origin The origin of the request, i.e. the app name.
   * @return The time in seconds the reset link is valid. 0 if not supported.
   * @throws Exception If an error happens, see {@link ProductService} which ones.
   */
  public int passwordReset(String email, String origin) {
    Options options = getDefaultOptions();
    options.anonymous = true;
    Map<String, String> arg = new HashMap<>();
    arg.put("origin", origin);
    Map result = super.execute("null", "passwordreset", email, arg, Map.class, options, null);
    Object livetime = result.get("livetime");
    return livetime instanceof Integer ? (Integer) livetime : 0;
  }


  /**
   * Set a listener when interested to get notified about merchants around a location.
   * To set a location use {@link #updateLocation(com.secucard.connect.product.general.model.Location)}.
   * Set to null to remove a listener.
   */
  public void onMerchantsChanged(EventListener<MerchantList> listener) {
    context.eventDispatcher.registerListener(MerchantList.class, listener);
  }

  public static abstract class AccountEventListener extends AbstractEventListener<Account> {

    @Override
    public boolean accept(Event<Account> event) {
      return EVENT_TYPE_BEACON_MONITOR.equals(event.getType()) && META_DATA.getObject().equals(event.getTarget());
    }
  }

}
