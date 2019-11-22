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
    if (log == null) {
      log = new HashMap<String, String>();
    }
    log.put("SDK", "secucard-connect-java-sdk");
    log.put("SDK-VERSION", SecucardConnect.VERSION);
    log.put("JAVA-VENDOR", System.getProperty("java.vendor"));
    log.put("JAVA-VERSION", System.getProperty("java.version"));

    return super.executeToBool(App.APP_ID_SUPPORT, "callBackend", "sendLog", log, new Options(Options.CHANNEL_STOMP), null);
  }

  public boolean ping(String channel) {
    if (channel == null) {
      channel = Options.CHANNEL_REST;
    }

    return super.executeToBool(App.APP_ID_SUPPORT, "callBackend", "ping", null, new Options(channel), null);
  }
}
