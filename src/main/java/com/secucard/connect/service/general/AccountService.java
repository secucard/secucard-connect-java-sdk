package com.secucard.connect.service.general;

import com.secucard.connect.Callback;
import com.secucard.connect.ClientContext;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.model.general.Account;
import com.secucard.connect.model.general.BeaconEnvironment;
import com.secucard.connect.model.general.Location;
import com.secucard.connect.model.general.MerchantList;
import com.secucard.connect.model.transport.Result;
import com.secucard.connect.service.AbstractService;

import java.util.List;

public class AccountService extends AbstractService {


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
        return getChannel().createObject(account, callback);
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
      return getRestChannel().getObject(Account.class, id, callback);
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
      return getRestChannel().updateObject(account, callback);
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
  public void deleteAccount(String accountId, Callback callback) {
    delete(Account.class, accountId, callback, ClientContext.REST);
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

  public boolean updateGCM(final String accountId, final String registrationId, Callback<Boolean> callback) {
//        return new Result2BooleanInvoker() {
//            @Override
//            protected Result handle(Callback<Result> callback) throws Exception {
//                return getStompChannel().updateObject(Account.class, accountId, "location", null, registrationId, Result.class, callback);
//            }
//        }.invokeAndConvert(callback);
    return true;
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
