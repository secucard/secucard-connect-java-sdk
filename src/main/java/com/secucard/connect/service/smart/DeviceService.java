package com.secucard.connect.service.smart;

import com.secucard.connect.Callback;
import com.secucard.connect.model.smart.Device;
import com.secucard.connect.model.transport.Result;
import com.secucard.connect.service.AbstractService;

public class DeviceService extends AbstractService {

  /**
   * Register a device.
   *
   * @param device The device to register.
   * @return True if successfully, false else.
   */
  public boolean registerDevice(final Device device, Callback<Boolean> callback) {
    return new Result2BooleanInvoker() {
      @Override
      protected Result handle(Callback<Result> callback) throws Exception {
        return getStompChannel().execute(Device.class, "me", "register", null, device, Result.class, callback);
      }
    }.invokeAndConvert(callback);
  }
}
