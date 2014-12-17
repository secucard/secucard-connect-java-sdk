package com.secucard.connect.service.smart;

import com.secucard.connect.Callback;
import com.secucard.connect.model.smart.Device;
import com.secucard.connect.model.transport.Result;
import com.secucard.connect.service.AbstractService;
import com.secucard.connect.util.Converter;

public class DeviceService extends AbstractService {

  /**
   * Register a device.
   *
   * @param device The device to register.
   * @return True if successfully, false else.
   */
  public boolean registerDevice(Device device, Callback callback) {
    try {
      Converter<Result, Boolean> converter = new Converter<Result, Boolean>() {
        @Override
        public Boolean convert(Result value) {
          return value == null ? Boolean.FALSE : Boolean.parseBoolean(value.getResult());
        }
      };
      // todo: switch to id, static just for test
      Result result = getStompChannel().execute(Device.class, "me", "register", null, device, Result.class,
          getCallbackAdapter(callback, converter));
      return converter.convert(result);
    } catch (Exception e) {
      handleException(e, callback);
    }
    return false;
  }
}
