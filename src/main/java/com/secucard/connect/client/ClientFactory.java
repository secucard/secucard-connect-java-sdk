package com.secucard.connect.client;

import com.secucard.connect.SecuException;
import com.secucard.connect.channel.PathResolverImpl;
import com.secucard.connect.channel.rest.RestChannel;
import com.secucard.connect.channel.rest.StaticGenericTypeResolver;
import com.secucard.connect.channel.stomp.JsonBodyMapper;
import com.secucard.connect.channel.stomp.SecuStompChannel;
import com.secucard.connect.storage.SimpleFileDataStorage;

public class ClientFactory {
  private ClientContext context;

  private static ClientFactory ourInstance = new ClientFactory();

  public static ClientFactory getInstance() {
    return ourInstance;
  }

  private ClientFactory() {
  }

  public ClientFactory init(ClientConfiguration config) {
    if (config == null) {
      throw new SecuException("Configuration  must not be null.");
    }
    context = new ClientContext();
    context.setConfig(config);
    context.setDataStorage(new SimpleFileDataStorage());
    context.setPathResolver(new PathResolverImpl());
    context.setRestChannel(createRestChannel());
    context.setStompChannel(createStompChannel());
    return this;
  }

  private SecuStompChannel createStompChannel() {
    SecuStompChannel channel = new SecuStompChannel(context.getConfig().getStompConfiguration());
    channel.setBodyMapper(new JsonBodyMapper());
    channel.setPathResolver(context.getPathResolver());
    channel.setAuthProvider(context.getRestChannel());
    return channel;
  }

  private RestChannel createRestChannel() {
    RestChannel channel = new RestChannel(context.getConfig().getRestConfiguration());
    channel.setPathResolver(context.getPathResolver());
    channel.setTypeResolver(new StaticGenericTypeResolver());
    channel.setStorage(context.getDataStorage());
    return channel;
  }

  public <T extends BaseClient> T create(Class<T> type) {
    T client = null;
    try {
      client = type.newInstance();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    client.setContext(context);

    return client;
  }
}
