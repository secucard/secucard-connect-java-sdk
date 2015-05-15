package com.secucard.connect.service.general;

import com.secucard.connect.Callback;
import com.secucard.connect.channel.Channel;
import com.secucard.connect.event.AbstractEventListener;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.model.general.*;
import com.secucard.connect.model.transport.Result;
import com.secucard.connect.service.AbstractService;

import java.util.List;

public class AccountService extends AbstractService {

  public static final String EVENT_TYPE_BEACON_MONITOR = "BeaconMonitor";

  public void onBeaconMonitor(AccountEventListener listener) {
    getEventDispatcher().registerListener(Account.OBJECT + EVENT_TYPE_BEACON_MONITOR, listener);
  }

  /**
   * Creating a account.
   *
   * @param account The account data to save.
   * @return The new account. Use this instance for further processing rather the provided.
   */
  public Account createAccount(final Account account, Callback<Account> callback) {
    return new ServiceTemplate(Channel.REST, false).create(account, callback);
  }

  /**
   * Gets the account with the given id
   *
   * @param id The account id
   * @return The account with the given id
   */
  public Account getAccount(String id, Callback<Account> callback) {
    return new ServiceTemplate(Channel.REST, false).get(Account.class, id, callback);
  }

  /**
   * Updates the account
   *
   * @param account Updated account
   * @return True if successfully updated, false else.
   */
  public Account updateAccount(Account account, Callback<Account> callback) {
    return new ServiceTemplate(Channel.REST, false).update(account, callback);
  }

  /**
   * Delete the account
   *
   * @param accountId Account ID
   */
  public void deleteAccount(final String accountId, Callback callback) {
    new ServiceTemplate(Channel.REST, false).delete(Account.class, accountId, callback);
  }

  /**
   * Updates the location of a account.
   *
   * @param accountId The account to update.
   * @param location  The new geo location to set.
   * @return True if successfully updated, false else.
   */
  public boolean updateLocation(String accountId, Location location) {
    return new ServiceTemplate(Channel.STOMP).updateToBoolean(Account.class, accountId, "location", null, location,
        Result.class, null);
  }

  /**
   * Updates the beacons of the account
   *
   * @param accountId  Account ID
   * @param beaconList List of beacons near by
   * @return True if successfully updated, false else.
   */
  public boolean updateBeacons(String accountId, List<BeaconEnvironment> beaconList) {
    return new ServiceTemplate(Channel.STOMP).updateToBoolean(Account.class, "me", "beaconEnvironment", null,
        beaconList, Result.class, null);
  }

  public boolean updateGCM(String accountId, Object objectArg) {
    return new ServiceTemplate(Channel.STOMP).updateToBoolean(Account.class, accountId, "gcm", null, objectArg,
        Result.class, null);
  }

  /**
   * Set a listener when interested to get notified about merchants around a location.
   * To set a location use {@link #updateLocation(String, Location)}.
   * Set to null to remove a listener.
   */
  public void onMerchantsChanged(EventListener<MerchantList> listener) {
    getEventDispatcher().registerListener(MerchantList.class, listener);
  }


  public static abstract class AccountEventListener extends AbstractEventListener<Event<Account>> {
    @Override
    public boolean accept(Event<Account> event) {
      return EVENT_TYPE_BEACON_MONITOR.equals(event.getType()) && Account.OBJECT.equals(event.getTarget());
    }
  }
}
