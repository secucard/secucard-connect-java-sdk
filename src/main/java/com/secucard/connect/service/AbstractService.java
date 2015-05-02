package com.secucard.connect.service;

import com.secucard.connect.Callback;
import com.secucard.connect.ClientContext;
import com.secucard.connect.ExceptionHandler;
import com.secucard.connect.ServiceOperations;
import com.secucard.connect.auth.AuthProvider;
import com.secucard.connect.channel.Channel;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.event.Events;
import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.QueryParams;
import com.secucard.connect.model.SecuObject;
import com.secucard.connect.model.transport.Result;
import com.secucard.connect.util.Converter;
import com.secucard.connect.util.Log;
import com.secucard.connect.util.ThreadLocalUtil;

import java.util.List;

public abstract class AbstractService {
  protected ClientContext context;
  protected final Log LOG = new Log(getClass());

  public void setContext(ClientContext context) {
    this.context = context;
  }

  /**
   * See {@link ClientContext#getChannel(String)}
   */
  protected Channel getChannel() {
    return context.getChannel(null);
  }

  /**
   * See {@link ClientContext#getChannel(String)}
   */
  protected Channel getStompChannel() {
    return context.getChannel(Channel.STOMP);
  }

  /**
   * See {@link ClientContext#getChannel(String)}
   */
  protected Channel getRestChannel() {
    return context.getChannel(Channel.REST);
  }

  protected AuthProvider getAuthProvider() {
    return context.getAuthProvider();
  }

  /**
   * Assign client context to current thread of execution so it can be accessed from objects not having a
   * reference to the client.
   */
  protected void setContext() {
    ThreadLocalUtil.set(ClientContext.class.getName(), context);
  }

  /**
   * Remove all data assigned to threads.
   */
  protected void clear() {
    ThreadLocalUtil.remove();
  }

  /**
   * Service initialization method to override for special initialization. The default does nothing.
   * Should get called after construction of a service instance and when all dependencies are set.
   */
  public void init() {

  }

  public void setEventListener(final EventListener eventListener) {
    context.getEventDispatcher().setEventListener(Events.ANY, eventListener);
  }


  /**
   * Class to use when accessing any service operation.
   * Directs to a given channel or the default channel (set in config)
   */
  public class ServiceTemplate implements ServiceOperations {
    private String channel;
    private boolean anonymous = false;

    public ServiceTemplate() {
    }

    public ServiceTemplate(String channel) {
      this.channel = channel;
    }

    public ServiceTemplate(String channel, boolean anonymous) {
      this.channel = channel;
      this.anonymous = anonymous;
    }

    /**
     * Override to implement further handling of results obtained by one of the service operations.
     * The default does nothing.
     * This code will be executed if a callback was used or not.
     * If a callback was used, the callback returns after executing this method.
     *
     * @param arg The service operation result. This is always an instance of the original result type,
     *            not the converted type if any conversion takes place (ObjectList -> List for example).
     */
    protected void onResult(Object arg) {
    }

    protected void handleFailure(Throwable throwable) {
      ExceptionHandler exceptionHandler = context.getExceptionHandler();
      if (exceptionHandler != null) {
        exceptionHandler.handle(throwable);
      }
    }

    /**
     * Execute channel operation implemented as a invocation action.
     * This allows for implementing arbitrary channel operations within an context of a specific channel and result
     * callback without caring about the exception and result handling.
     *
     * @param invocation Specifies the action to execute.
     * @param callback   Callback which gets notified about the results. Null for no callback.
     * @param <T>        The result type of the execution.
     * @return The result of the execution. Null if a callback is provided.
     */
    private <T> T execute(ChannelInvocation<T> invocation, final Callback<T> callback) {

      // must wrap in proxy callback if callback was provided
      Callback<T> proxyCallback = callback == null ? null : new Callback<T>() {
        @Override
        public void completed(T result) {
          onResult(result);
          callback.completed(result);
        }

        @Override
        public void failed(Throwable cause) {
          handleFailure(cause);
          callback.failed(cause);
        }
      };

      if (anonymous) {
        ThreadLocalUtil.set("anonymous", Boolean.TRUE);
      }

      try {
        T result = invocation.doInContext(context.getChannel(channel), proxyCallback);
        if (callback == null) {
          onResult(result);
          return result;
        }
      } catch (Throwable t) {
        handleFailure(t);
        if (callback == null) {
          throw t;
        } else {
          callback.failed(t);
        }
      } finally {
        ThreadLocalUtil.set("anonymous", null);
      }

      return null;
    }

    private <T> List<T> executeToList(ChannelInvocation<ObjectList<T>> inv, final Callback<List<T>> callback) {
      return execute(inv, new Converter.ToListConverter<T>(), callback);
    }

    private Boolean executeToBoolean(ChannelInvocation<Result> inv, final Callback<Boolean> callback) {
      return execute(inv, new Converter.ToBooleanConverter(), callback);
    }

    private <FROM, TO> TO execute(ChannelInvocation<FROM> inv, final Converter<FROM, TO> conv,
                                  final Callback<TO> callback) {
      Callback<FROM> convertingCallback = callback == null ? null : new Callback<FROM>() {
        @Override
        public void completed(FROM result) {
          callback.completed(conv.convert(result));
        }

        @Override
        public void failed(Throwable cause) {
          callback.failed(cause);
        }
      };
      FROM result = execute(inv, convertingCallback);
      return conv.convert(result);
    }

    @Override
    public <T> T get(final Class<T> targetType, final String objectId, Callback<T> callback) {
      return execute(new ChannelInvocation<T>() {
        @Override
        public T doInContext(Channel channel, Callback<T> resultCallback) {
          return channel.get(targetType, objectId, resultCallback);
        }
      }, callback);
    }

    @Override
    public <T> ObjectList<T> getList(final Class<T> targetType, final QueryParams queryParams,
                                     final Callback<ObjectList<T>> callback) {
      return execute(new ChannelInvocation<ObjectList<T>>() {
        @Override
        public ObjectList<T> doInContext(Channel channel, Callback<ObjectList<T>> resultCallback) {
          return channel.getList(targetType, queryParams, resultCallback);
        }
      }, callback);
    }

    public <T> List<T> getAsList(final Class<T> targetType, final QueryParams queryParams,
                                 Callback<List<T>> callback) {
      return executeToList(new ChannelInvocation<ObjectList<T>>() {
        @Override
        public ObjectList<T> doInContext(Channel channel, Callback<ObjectList<T>> resultCallback) {
          return channel.getList(targetType, queryParams, resultCallback);
        }
      }, callback);
    }

    @Override
    public <T> T create(final T object, Callback<T> callback) {
      return execute(new ChannelInvocation<T>() {
        @Override
        public T doInContext(Channel channel, Callback<T> resultCallback) {
          return channel.create(object, resultCallback);
        }
      }, callback);

    }

    @Override
    public <T extends SecuObject> T update(final T object, Callback<T> callback) {
      return execute(new ChannelInvocation<T>() {
        @Override
        public T doInContext(Channel channel, Callback<T> resultCallback) {
          return channel.update(object, resultCallback);
        }
      }, callback);

    }

    @Override
    public <T> T update(final Class targetType, final String objectId, final String action, final String actionArg,
                        final Object arg, final Class<T> returnType, Callback<T> callback) {
      return execute(new ChannelInvocation<T>() {
        @Override
        public T doInContext(Channel channel, Callback<T> resultCallback) {
          return channel.update(targetType, objectId, action, actionArg, arg, returnType, resultCallback);
        }
      }, callback);

    }

    public Boolean updateToBoolean(final Class targetType, final String objectId, final String action,
                                   final String actionArg, final Object arg, final Class<Result> returnType,
                                   Callback<Boolean> callback) {
      return executeToBoolean(new ChannelInvocation<Result>() {
        @Override
        public Result doInContext(Channel channel, Callback<Result> resultCallback) {
          return channel.update(targetType, objectId, action, actionArg, arg, returnType, resultCallback);
        }
      }, callback);
    }

    @Override
    public void delete(final Class targetType, final String objectId, Callback<Void> callback) {
      execute(new ChannelInvocation<Void>() {
        @Override
        public Void doInContext(Channel channel, Callback<Void> resultCallback) {
          channel.delete(targetType, objectId, resultCallback);
          return null;
        }
      }, callback);
    }

    @Override
    public void delete(final Class targetType, final String objectId, final String action, final String actionArg,
                       Callback<Void> callback) {
      execute(new ChannelInvocation<Void>() {
        @Override
        public Void doInContext(Channel channel, Callback<Void> resultCallback) {
          channel.delete(targetType, objectId, action, actionArg, resultCallback);
          return null;
        }
      }, callback);
    }

    @Override
    public <T> T execute(final Class targetType, final String objectId, final String action, final String actionArg,
                         final Object arg, final Class<T> returnType, Callback<T> callback) {
      return this.execute(new ChannelInvocation<T>() {
        @Override
        public T doInContext(Channel channel, Callback<T> resultCallback) {
          return channel.execute(targetType, objectId, action, actionArg, arg, returnType, resultCallback);
        }
      }, callback);
    }

    public Boolean executeToBoolean(final Class targetType, final String objectId, final String action,
                                    final String actionArg, final Object arg, final Class<Result> returnType,
                                    Callback<Boolean> callback) {
      return executeToBoolean(new ChannelInvocation<Result>() {
        @Override
        public Result doInContext(Channel channel, Callback<Result> resultCallback) {
          return channel.execute(targetType, objectId, action, actionArg, arg, returnType, resultCallback);
        }
      }, callback);
    }

    @Override
    public <T> T execute(final String appId, final String action, final Object arg, final Class<T> returnType,
                         Callback<T> callback) {
      return execute(new ChannelInvocation<T>() {
        @Override
        public T doInContext(Channel channel, Callback<T> resultCallback) {
          return channel.execute(appId, action, arg, returnType, resultCallback);
        }
      }, callback);
    }

    public Boolean executeToBoolean(final String appId, final String action, final Object arg,
                                    final Class<Result> returnType, Callback<Boolean> callback) {
      return executeToBoolean(new ChannelInvocation<Result>() {
        @Override
        public Result doInContext(Channel channel, Callback<Result> resultCallback) {
          return channel.execute(appId, action, arg, returnType, resultCallback);
        }
      }, callback);

    }
  }

  /**
   * Generic callback interface for executing channel call operating on a specific channel and callback.
   * This can be used for executing entire code blocks without caring about thrown exceptions or if a callback
   * was actually provided or not (null) - the caller must catch and delegate to the callback appropriately if exist.
   *
   * @param <T> The invocation result type.
   */
  private static interface ChannelInvocation<T> {
    T doInContext(Channel channel, Callback<T> resultCallback);
  }
}
