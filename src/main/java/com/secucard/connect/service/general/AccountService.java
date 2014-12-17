package com.secucard.connect.service.general;

import com.secucard.connect.Callback;
import com.secucard.connect.model.general.accounts.Account;
import com.secucard.connect.model.general.accounts.Location.Location;
import com.secucard.connect.model.transport.Result;
import com.secucard.connect.service.AbstractService;
import com.secucard.connect.util.Converter;

public class AccountService extends AbstractService {

  public boolean updateLocation(Location location, Callback<Boolean> callback) {

    Converter<Result, Boolean> converter = new Converter<Result, Boolean>() {
      @Override
      public Boolean convert(Result value) {
        return value == null ? Boolean.FALSE : Boolean.parseBoolean(value.getResult());
      }
    };

    try {
      Result result = getStompChannel().updateObject(Account.class, "me", "location", null, location, Result.class,
          getCallbackAdapter(callback, converter));
      return converter.convert(result);
    } catch (Exception e) {
      handleException(e, callback);
    }

    return false;
  }
}
