package com.secucard.connect.client;

import com.secucard.connect.SecuException;
import com.secucard.connect.channel.PathResolverImpl;
import com.secucard.connect.channel.rest.RestChannel;
import com.secucard.connect.channel.rest.StaticGenericTypeResolver;
import com.secucard.connect.channel.stomp.JsonBodyMapper;
import com.secucard.connect.channel.stomp.SecuStompChannel;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.storage.MemoryDataStorage;
import com.secucard.connect.storage.SimpleFileDataStorage;

import java.io.IOException;

/**
 * Main entry to the Java Secucard Connect API.
 */
public class Client {
  protected ClientContext context;
  private Thread heartbeatInvoker;
  private String id;

  private Client(final String id, ClientConfiguration configuration) {
    init(id, configuration);
  }

  public static Client create(String id, ClientConfiguration configuration) {
    return new Client(id, configuration);
  }

  public <T extends AbstractService> T createService(Class<T> type) {
    T client = null;
    try {
      client = type.newInstance();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    client.setContext(context);
    return client;
  }

  public String getId() {
    return id;
  }

  public void setEventListener(EventListener eventListener) {
    context.getStompChannel().setEventListener(eventListener);
  }

  public void connect() throws ConnectException {
    try {
      // first rest since it does auth
      context.getRestChannel().open();
      context.getStompChannel().open();
      startHeartBeat();
    } catch (IOException e) {
      throw new ConnectException(e);
    }
  }

  public void disconnect() {
    stopHeartBeat();
    context.getStompChannel().close();
    context.getRestChannel().close();
    // todo: clear data store?
  }

  protected void startHeartBeat() {
    final int heartBeatSec = context.getConfig().getHeartBeatSec();
    if (heartBeatSec != 0) {
      stopHeartBeat();
      heartbeatInvoker = new Thread() {
        @Override
        public void run() {
          while (!isInterrupted()) {
            context.getStompChannel().invoke("/ping");
            try {

              Thread.sleep(heartBeatSec * 1000);
            } catch (InterruptedException e) {
              break;
            }
          }
        }
      };
      heartbeatInvoker.start();
    }
  }

  protected void stopHeartBeat() {
    if (heartbeatInvoker != null && heartbeatInvoker.isAlive()) {
      heartbeatInvoker.interrupt();
      try {
        heartbeatInvoker.join();
      } catch (InterruptedException e) {
      }
    }
  }

  private void init(String id, ClientConfiguration config) {
    if (config == null) {
      throw new SecuException("Configuration  must not be null.");
    }
    this.id = id;
    context = new ClientContext();
    context.setConfig(config);
    try {
      context.setDataStorage(new SimpleFileDataStorage("/tmp/secu.store"));
      context.setDataStorage(new MemoryDataStorage());
    } catch (IOException e) {
      throw new SecuException("Error creating file storage", e);
    }
    context.setPathResolver(new PathResolverImpl());
    context.setRestChannel(createRestChannel());
    context.setStompChannel(createStompChannel());
  }

  private SecuStompChannel createStompChannel() {
    SecuStompChannel channel = new SecuStompChannel(id, context.getConfig().getStompConfiguration());
    channel.setBodyMapper(new JsonBodyMapper());
    channel.setPathResolver(context.getPathResolver());
    channel.setAuthProvider(context.getRestChannel());
    return channel;
  }

  private RestChannel createRestChannel() {
    RestChannel channel = new RestChannel(id, context.getConfig().getRestConfiguration());
    channel.setPathResolver(context.getPathResolver());
    channel.setTypeResolver(new StaticGenericTypeResolver());
    channel.setStorage(context.getDataStorage());
    return channel;
  }

}
