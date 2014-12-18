package com.secucard.connect.service.general;

import com.secucard.connect.Callback;
import com.secucard.connect.model.general.accounts.Account;
import com.secucard.connect.model.general.accounts.Location.Location;
import com.secucard.connect.model.transport.Result;
import com.secucard.connect.service.AbstractService;

public class AccountService extends AbstractService {

  public boolean updateLocation(final Location location, Callback<Boolean> callback) {
    return new Result2BooleanInvoker() {
      @Override
      protected Result handle(Callback<Result> callback) throws Exception {
        return getStompChannel().updateObject(Account.class, "me", "location", null, location, Result.class, callback);
      }
    }.invokeAndConvert(callback);
  }
}
