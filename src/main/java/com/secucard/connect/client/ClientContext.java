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

package com.secucard.connect.client;

import com.secucard.connect.auth.TokenManager;
import com.secucard.connect.event.EventDispatcher;
import com.secucard.connect.net.Channel;
import com.secucard.connect.net.util.JsonMapper;

import java.util.Map;

/**
 * Bundles all instances to work within client and service.
 */
public class ClientContext {
  public TokenManager tokenManager;
  public EventDispatcher eventDispatcher;
  public ExceptionHandler exceptionHandler;
  public Map<String, Channel> channels;
  public String defaultChannel;
  public JsonMapper jsonMapper;
  public Object runtimeContext;
  public String appId;
}
