package com.secucard.connect.product.general;

import com.secucard.connect.SecucardConnect;
import com.secucard.connect.client.ProductService;
import com.secucard.connect.net.Options;
import com.secucard.connect.product.general.model.App;
import java.util.HashMap;
import java.util.Map;

/**
 * Implements the general/apps operations.
 */

public class AppsService extends ProductService<App> {

  public static final ServiceMetaData<App> META_DATA = new ServiceMetaData<>("general", "apps", App.class);

  @Override
  public ServiceMetaData<App> getMetaData() {
    return META_DATA;
  }

  public boolean sendLogMessage(Map<String, String> log) {
    return super.context.channels.get(Options.CHANNEL_STOMP).sendLogMessage(log);
  }

  public boolean ping(String channel) {
    if (channel == null) {
      channel = Options.CHANNEL_REST;
    }

    return super.context.channels.get(channel).ping();
  }
}
