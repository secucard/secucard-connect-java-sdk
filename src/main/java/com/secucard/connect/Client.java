package com.secucard.connect;

import com.secucard.connect.model.ObjectList;
import com.secucard.connect.model.smart.Device;
import com.secucard.connect.model.smart.Ident;
import com.secucard.connect.model.smart.Result;
import com.secucard.connect.model.smart.Transaction;
import com.secucard.connect.rest.RestChannel;
import com.secucard.connect.rest.RestConfig;
import com.secucard.connect.rest.StaticGenericTypeResolver;
import com.secucard.connect.stomp.JsonBodyMapper;
import com.secucard.connect.stomp.SecuStompChannel;
import com.secucard.connect.stomp.StompConfig;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client {
  private final Map<ChannelName, Channel> channels = new HashMap<>(2);
  private final ClientConfig config;

  private Client(ClientConfig config) {
    this.config = config;
  }

  private void registerChannel(Channel channel, ChannelName name) {
    this.channels.put(name, channel);
  }

  public void setEventListener(EventListener eventListener) {
    selectChannnel(ChannelName.STOMP).setEventListener(eventListener);
  }

  public void connect() throws ConnectException {
    try {
      for (Channel channel : channels.values()) {
        channel.open();
      }
    } catch (IOException e) {
      throw new ConnectException(e);
    }
  }

  public void disconnect() {
    for (Channel channel : channels.values()) {
      channel.close();
    }
  }

  public boolean registerDevice(Device device) {
    return selectChannnel(ChannelName.STOMP).execute("register", new String[]{device.getId()}, device, null);
  }

  public List<Ident> getIdents() {
    ObjectList<Ident> idents = selectChannnel().findObjects(Ident.class, null);
    if (idents != null) {
      return idents.getList();
    }
    return null;
  }

  public Transaction createTransaction(Transaction transaction) {
    return selectChannnel().saveObject(transaction);
  }


  public Result startTransaction(Transaction transaction) {
    return selectChannnel().execute("start", new String[]{transaction.getId(), "demo"}, transaction, Result.class);
  }


  private Channel selectChannnel() {
    return selectChannnel(config.getDefaultChannel());
  }

  private Channel selectChannnel(ChannelName name) {
    if (channels.size() == 1) {
      return channels.values().iterator().next();
    }
    return channels.get(name);
  }

  public static Client create(ClientConfig config) {
    PathResolverImpl pathResolver = new PathResolverImpl();

    RestChannel restChannel = new RestChannel(config.getRestConfig());
    restChannel.setPathResolver(pathResolver);
    restChannel.setTypeResolver(new StaticGenericTypeResolver());

    SecuStompChannel stompChannel = new SecuStompChannel(config.getStompConfig());

    stompChannel.setBodyMapper(new JsonBodyMapper());
    stompChannel.setPathResolver(pathResolver);

    Client client = new Client(config);

    client.registerChannel(stompChannel, Client.ChannelName.STOMP);
    client.registerChannel(restChannel, Client.ChannelName.REST);

    return client;
  }


  public enum ChannelName {
    STOMP, REST
  }
}
