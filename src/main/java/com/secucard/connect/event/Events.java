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

package com.secucard.connect.event;

/**
 * Defines client common events and event related constants.
 */
public abstract class Events {

  // common event types
  public static final String TYPE_CHANGED = "changed";
  public static final String TYPE_ADDED = "added";
  public static final String TYPE_DISPLAY = "display";

  /**
   * Fired when the connection state of the client changes.
   */
  public static final class ConnectionStateChanged {
    public boolean connected;

    public ConnectionStateChanged(boolean connected) {
      this.connected = connected;
    }


    @Override
    public String toString() {
      return "ConnectionStateChanged{" +
          "connected=" + connected +
          '}';
    }
  }


}
