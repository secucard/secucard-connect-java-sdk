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

package com.secucard.connect.net;

import com.secucard.connect.client.Callback;

/**
 * Holds all options for an API call.
 */
public class Options {
  public static final String CHANNEL_REST = "rest";
  public static final String CHANNEL_STOMP = "stomp";
  public boolean anonymous = false;
  public boolean expand = false;
  public boolean eventListening = false;
  public String channel;
  public String clientId = null;
  public String actionId = null;
  public Integer timeOutSec = null;

  /**
   * Set an callback to be executed after a resource was successfully retrieved.
   */
  public Callback.Notify<?> resultProcessing;

  public static Options getDefault() {
    return new Options();
  }

  public Options() {
  }

  public Options(String channel) {
    this.channel = channel;
  }
}
