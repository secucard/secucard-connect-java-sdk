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


import com.secucard.connect.SecucardConnect;
import com.secucard.connect.client.Callback;
import com.secucard.connect.client.ClientContext;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.product.common.model.ObjectList;
import com.secucard.connect.product.common.model.QueryParams;
import com.secucard.connect.product.common.model.Result;
import com.secucard.connect.product.general.model.App;
import com.secucard.connect.util.Converter;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * The base class used to realize any type of server communication.
 */
public abstract class Channel {
  protected EventListener<Object> eventListener;
  protected ClientContext context;

  protected Channel(ClientContext context) {
    this.context = context;
  }

  /**
   * Retrieving an object of any type.
   *
   * @param method   The method to use.
   * @param params   The call parameters.
   * @param callback Callback for async processing.
   * @param <T>      The actual object type.
   * @return The object, null if a callback was used.
   */
  public abstract <T> T request(Method method, Params params, Callback<T> callback);


  /**
   * Retrieving a list of objects of any type.
   *
   * @param method   The method to use.
   * @param params   The call parameters.
   * @param callback Callback for async processing.
   * @param <T>      The actual object type.
   * @return The object list, null if a callback was used.
   */
  public abstract <T> ObjectList<T> requestList(Method method, Params params, Callback<ObjectList<T>> callback);


  /**
   * Registers a listener which gets called when a server side or other event happens.
   * Server side events may not be supported by some channels, e.g. REST based channels!
   * <p/>
   * Note that exceptions thrown by listeners methods may be swallowed by the calling thread code silently to prevent
   * breaking the event receiving process.
   *
   * @param listener The listener to notify.
   */
  public void setEventListener(EventListener<Object> listener) {
    eventListener = listener;
  }

  /**
   * Open the channel and its resources.
   */
  public abstract void open();

  /**
   * Close channel and release resources.
   */
  public abstract void close();

  public static enum Method {
    GET, CREATE, UPDATE, DELETE, EXECUTE
  }

  public static class Params {
    public String[] object; // target object type, consisting of 2 parts
    public String objectId; // id of resource
    public String appId;
    public String action;
    public String actionArg;
    public Object data; // payload
    public Class returnType; // object type of response
    public QueryParams queryParams;
    public Options options;

    public Params() {
    }

    // some typical used constructors ----------------------------------------------------------------------------------


    public Params(String[] object, String objectId, String appId, String action, String actionArg, Object data,
                  Class returnType, QueryParams queryParams, Options options) {
      this.object = object;
      this.objectId = objectId;
      this.appId = appId;
      this.action = action;
      this.actionArg = actionArg;
      this.data = data;
      this.returnType = returnType;
      this.queryParams = queryParams;
      this.options = options;
    }

    public Params(String[] object, String objectId, Options options) {
      this(object, objectId, null, null, null, null, null, null, options);
    }

    public Params(String[] object, String objectId, Class returnType, Options options) {
      this(object, objectId, null, null, null, null, returnType, null, options);
    }

    public Params(String[] object, QueryParams queryParams, Class returnType, Options options) {
      this(object, null, null, null, null, null, returnType, queryParams, options);
    }

    public Params(String[] object, Object data, Class returnType, Options options) {
      this(object, null, null, null, null, data, returnType, null, options);
    }

    public Params(String[] object, String objectId, Object data, Class returnType, Options options) {
      this(object, objectId, null, null, null, data, returnType, null, options);
    }

    public Params(String[] object, String objectId, String action, String actionArg, Object data,
                  Class returnType, Options options) {
      this(object, objectId, null, action, actionArg, data, returnType, null, options);
    }

    public Params(String[] object, String objectId, String action, String actionArg, Options options) {
      this(object, objectId, null, action, actionArg, null, null, null, options);
    }

    public static Params forApp(String appId, String action, Object payload, Class returnType, Options options) {
      return new Params(null, null, appId, action, null, payload, returnType, null, options);
    }

    @Override
    public String toString() {
      return "Channel.Params{" +
          "object='[" + object[0] + "', '" + object[1] + "']" +
          ", objectId='" + objectId + '\'' +
          ", action='" + action + '\'' +
          ", actionArg='" + actionArg + '\'' +
          ", returnType='" + returnType.toString() + '\'' +
          '}';
    }
  }


  /**
   * Joins the target parts with the separator and capitalizes.
   */
  protected static String buildTarget(String[] parts, char separator) {
    if (parts == null || parts.length < 2) {
      throw new IllegalArgumentException("Invalid target specification.");
    }
    return parts[0] + separator + parts[1];
  }

  public boolean sendLogMessage(String message, String level) {
    if (message == null) {
      return false;
    }

    if (level == null) {
      level = "DEBUG";
    }

    Map<String, String> log = new HashMap<>();
    log.put("level", level);
    log.put("message", message);
    log.put("timestamp", Instant.now().toString());

    return this.sendLogMessage(log);
  }

  public boolean sendLogMessage(Map<String, String> log) {
    if (log == null) {
      log = new HashMap<>();
    }
    log.put("SDK", "secucard-connect-java-sdk");
    log.put("SDK-VERSION", SecucardConnect.VERSION);
    log.put("JAVA-VENDOR", System.getProperty("java.vendor"));
    log.put("JAVA-VERSION", System.getProperty("java.version"));

    Options options = Options.getDefault();
    options.timeOutSec = 5; // should timeout sooner as by config to detect connection failure

    try {
      Result result = this.request(
          Method.EXECUTE,
          new Params(
              new String[]{"general", "apps"},
              App.APP_ID_SUPPORT,
              "callBackend",
              "sendLog",
              log,
              Result.class,
              options
          ),
          null
      );

      Converter<Result, Boolean> converter = Converter.RESULT2BOOL;
      return converter.convert(result);
    } catch (Throwable e) {
      return false;
    }
  }

  public boolean ping() {
    Options options = Options.getDefault();
    options.timeOutSec = 5; // should timeout sooner as by config to detect connection failure

    try {
      Result result = this.request(
          Method.EXECUTE,
          new Params(
              new String[]{"general", "apps"},
              App.APP_ID_SUPPORT,
              "callBackend",
              "ping",
              null,
              Result.class,
              options
          ),
          null
      );

      Converter<Result, Boolean> converter = Converter.RESULT2BOOL;
      return converter.convert(result);
    } catch (Throwable e) {
      return false;
    }
  }
}
