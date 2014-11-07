package com.secucard.connect;

import com.secucard.connect.auth.OAuthClientCredentials;
import com.secucard.connect.rest.RestChannel;
import com.secucard.connect.rest.RestConfig;
import com.secucard.connect.rest.StaticGenericTypeResolver;
import com.secucard.connect.stomp.JsonBodyMapper;
import com.secucard.connect.stomp.SecuStompChannel;
import com.secucard.connect.stomp.StompConfig;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BaseClient {
  protected ClientConfig config;

  private final Map<ChannelName, Channel> channels = new HashMap<>(2);

  public void setConfig(ClientConfig config) {
    this.config = config;
  }

  protected void registerChannel(Channel channel, ChannelName name) {
    this.channels.put(name, channel);
  }

  public void setEventListener(EventListener eventListener) {
    selectChannnel(ChannelName.STOMP).setEventListener(eventListener);
  }

  public void connect() throws ConnectException {
    try {
      // first rest since it does auth
      selectChannnel(ChannelName.REST).open();
      selectChannnel(ChannelName.STOMP).open();
    } catch (IOException e) {
      throw new ConnectException(e);
    }
  }

  public void disconnect() {
    for (Channel channel : channels.values()) {
      channel.close();
    }
  }

  public Channel selectChannnel() {
    return selectChannnel(config.getDefaultChannel());
  }

  public Channel selectChannnel(ChannelName name) {
    if (channels.size() == 1) {
      return channels.values().iterator().next();
    }
    return channels.get(name);
  }

  public enum ChannelName {
    STOMP, REST
  }

  public static <T extends BaseClient> T create(ClientConfig config, Class<T> type) {

    if(config == null) {
      config = getDefaultConfig();
    }

    PathResolverImpl pathResolver = new PathResolverImpl();

    RestChannel restChannel = new RestChannel(config.getRestConfig());
    restChannel.setPathResolver(pathResolver);
    restChannel.setTypeResolver(new StaticGenericTypeResolver());

    SecuStompChannel stompChannel = new SecuStompChannel(config.getStompConfig());

    stompChannel.setBodyMapper(new JsonBodyMapper());
    stompChannel.setPathResolver(pathResolver);
    stompChannel.setAuthProvider(restChannel);

    T client = null;
    try {
      client = type.newInstance();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    client.registerChannel(stompChannel, Client.ChannelName.STOMP);
    client.registerChannel(restChannel, Client.ChannelName.REST);

    client.setConfig(config);

    return client;
  }

  public static ClientConfig getDefaultConfig() {

    OAuthClientCredentials clientCredentials = new OAuthClientCredentials(
        "webapp", "821fc7042ec0ddf5cc70be9abaa5d6d311db04f4679ab56191038cb6f7f9cb7c");

    ClientConfig config = new ClientConfig(
        new RestConfig("https://core-dev10.secupay-ag.de/app.core.connector/api/v2",
            "https://core-dev10.secupay-ag.de/app.core.connector/oauth/token", clientCredentials),
        new StompConfig("dev10.secupay-ag.de", null, 61614, "/exchange/connect.api/", "guest", "guest", 0, true, true,
            "/temp-queue/main", 20, 60, 360, 5));

    return config;
  }

}
