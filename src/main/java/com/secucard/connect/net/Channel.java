package com.secucard.connect.net;


import com.secucard.connect.client.Callback;
import com.secucard.connect.client.ClientContext;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.product.common.model.ObjectList;
import com.secucard.connect.product.common.model.QueryParams;
import com.secucard.connect.util.Log;

/**
 * The base class used to realize any type of server communication.
 */
public abstract class Channel {
  protected EventListener<Object> eventListener;
  protected final String id;
  protected ClientContext context;

  protected Channel(String id, ClientContext context) {
    this.id = id;
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
}
