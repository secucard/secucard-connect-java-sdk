package com.secucard.connect.service.general;

import com.secucard.connect.Callback;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.model.general.accounts.Account;
import com.secucard.connect.model.general.accounts.Location.Location;
import com.secucard.connect.model.general.accounts.MerchantList;
import com.secucard.connect.model.transport.Result;
import com.secucard.connect.service.AbstractService;

public class AccountService extends AbstractService {

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
