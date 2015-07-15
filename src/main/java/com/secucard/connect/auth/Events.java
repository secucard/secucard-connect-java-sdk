package com.secucard.connect.auth;

import com.secucard.connect.auth.model.DeviceAuthCode;

/**
 * Define events fired by auth process.
 *
 * @see #EVENT_AUTH_OK
 * @see #EVENT_AUTH_PENDING
 * @see #EVENT_TYPE_DEVICE_CODES
 */
public abstract class Events {

  /**
   * Fired when an authentication succeeded.
   */
  public static final String EVENT_AUTH_OK = "AUTH_OK";

  /**
   * Fired when an authentication is pending.
   */
  public static final String EVENT_AUTH_PENDING = "AUTH_PENDING";

  /**
   * Instances are fired to provide obtained device codes to the user.
   */
  public static final Class<DeviceAuthCode> EVENT_TYPE_DEVICE_CODES = DeviceAuthCode.class;
}
