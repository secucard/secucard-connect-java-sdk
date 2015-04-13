package com.secucard.connect.service.general;

import com.secucard.connect.Callback;
import com.secucard.connect.channel.rest.RestChannelBase;
import com.secucard.connect.event.EventHandler;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.model.general.*;
import com.secucard.connect.model.transport.Result;
import com.secucard.connect.service.AbstractService;

import java.util.List;

public class AccountService extends AbstractService {

  public static final String TYPE_BEACON_MONITOR = "BeaconMonitor";

  public static final String ID = Account.OBJECT + TYPE_BEACON_MONITOR;

  public void onBeaconMonitor(final Callback<Event> callback) {
    addOrRemoveEventHandler(ID, new AccountEventEventHandler(callback), callback);
  }

  private class AccountEventEventHandler extends EventHandler<Event, Event> {
    public AccountEventEventHandler(Callback<Event> callback) {
      super(callback);
    }

    @Override
    public boolean accept(Event event) {
      return TYPE_BEACON_MONITOR.equals(event.getType()) && Account.OBJECT.equals(event.getTarget());
    }

    @Override
    public void handle(Event event) {
      completed(event);
    }
  }

  /**
   * Creating a account.
   *
   * @param account The account data to save.
   * @return The new account. Use this instance for further processing rather the provided.
   */
  public Account createAccount(final Account account, Callback<Account> callback) {
    return new Invoker<Account>() {
      @Override
      protected Account handle(Callback<Account> callback) throws Exception {
        RestChannelBase channel = (RestChannelBase) getRestChannel();
        channel.setSecure(false);
        return channel.createObject(account, callback);
      }
    }.invoke(callback);
  }

  /**
   * Gets the account with the given id
   *
   * @param id The account id
   * @return The account with the given id
   */
  public Account getAccount(String id, Callback<Account> callback) {
    try {
      RestChannelBase channel = (RestChannelBase) getRestChannel();
      channel.setSecure(true);
      return channel.getObject(Account.class, id, callback);
    } catch (Exception e) {
      handleException(e, callback);
    }
    return null;
  }

  /**
   * Updates the account
   *
   * @param account Updated account
   * @return True if successfully updated, false else.
   */
  public Account updateAccount(Account account, Callback<Account> callback) {
    try {
      RestChannelBase channel = (RestChannelBase) getRestChannel();
      channel.setSecure(true);
      return channel.updateObject(account, callback);
    } catch (Exception e) {
      handleException(e, callback);
    }

    return null;
  }

  /**
   * Delete the account
   *
   * @param accountId Account ID
   */
  public void deleteAccount(final String accountId, Callback callback) {
    new Invoker<Void>() {
      @Override
      protected Void handle(Callback<Void> callback11) throws Exception {
        RestChannelBase channel = (RestChannelBase) getRestChannel();
        channel.setSecure(true);
        channel.deleteObject(Account.class, accountId, callback11);
        return null;
      }
    }.invoke(callback);
  }

  /**
   * Updates the location of a account.
   *
   * @param accountId The account to update.
   * @param location  The new geo location to set.
   * @return True if successfully updated, false else.
   */
  public boolean updateLocation(String accountId, Location location) {
    try {
      Result result = getStompChannel().updateObject(Account.class, accountId, "location", null, location, Result.class,
          null);
      return Boolean.parseBoolean(result.getResult());
    } catch (Throwable e) {
      handleException(e, null);
    }
    return false;
  }

  /**
   * Updates the beacons of the account
   *
   * @param accountId  Account ID
   * @param beaconList List of beacons near by
   * @return True if successfully updated, false else.
   */
  public boolean updateBeacons(String accountId, List<BeaconEnvironment> beaconList) {
    try {
      Result result = getStompChannel().updateObject(Account.class, "me", "beaconEnvironment", null, beaconList, Result.class,
          null);
      return Boolean.parseBoolean(result.getResult());
    } catch (Throwable e) {
      handleException(e, null);
    }
    return false;
  }

  public boolean updateGCM(String accountId, Object objectArg) {
    try {
      Result result = getStompChannel().updateObject(Account.class, accountId, "gcm", null, objectArg, Result.class,
          null);
      return Boolean.parseBoolean(result.getResult());
    } catch (Throwable e) {
      handleException(e, null);
    }
    return false;
  }


  /**
   * todo: may change, right now this is just set up to demonstrate how the event handling as part of each service product API would work in principle
   * Set a listener when interested to get notified about merchants around a location.
   * To set a location use {@link #updateLocation(String, Location)}.
   * Set to null to remove a listener.
   */
  public void setMerchantsChangedListener(EventListener<MerchantList> listener) {
    setEventListener(MerchantList.class, listener);
  }
}
