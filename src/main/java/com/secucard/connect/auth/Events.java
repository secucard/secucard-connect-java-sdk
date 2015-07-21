/*
 * Copyright (c) 2015. hp.weber GmbH & Co secucard KG (www.secucard.com)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
