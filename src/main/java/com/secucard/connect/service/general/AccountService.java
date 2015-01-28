package com.secucard.connect.service.general;

import com.secucard.connect.Callback;
import com.secucard.connect.model.general.accounts.Account;
import com.secucard.connect.model.general.accounts.Location.Location;
import com.secucard.connect.model.transport.Result;
import com.secucard.connect.service.AbstractService;

public class AccountService extends AbstractService {

  /**
   * Updates the location of a account.
   * Note: If the update was succesfully performed an event of type {@code Event<MerchantList>}
   * and with id = "Publicmerchants.Aroundme" will happen afterwards, which brings up a list of merchant names
   * around the new location.
   *
   * @param accountId The account to update.
   * @param location  The new geo location to set.
   * @param callback  Callback for async invocation.
   * @return True if sucsessfully updated, false else.
   * todo: makes callback param sense here? method returns fast and results will be delivered by event anyway, so no async needed
   */
  public boolean updateLocation(final String accountId, final Location location, Callback<Boolean> callback) {
    return new Result2BooleanInvoker() {
      @Override
      protected Result handle(Callback<Result> callback) throws Exception {
        return getStompChannel().updateObject(Account.class, accountId, "location", null, location, Result.class, callback);
      }
    }.invokeAndConvert(callback);
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
}
