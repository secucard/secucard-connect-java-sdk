/**
 * Copyright 2014 hp.weber GmbH & Co secucard KG (www.secucard.com)
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.secucard.connect.net.stomp.client;

/**
 * Indicates that the STOMP connection could not be established within a given time.
 */
public class ConnectionTimeoutException extends RuntimeException {
  public ConnectionTimeoutException() {
  }

  public ConnectionTimeoutException(String message) {
    super(message);
  }
}
