package com.secucard.connect.util;

import com.secucard.connect.channel.rest.RestChannelBase;
import com.secucard.connect.storage.DataStorage;

import java.io.InputStream;

public class ResourceDownloader {
  private RestChannelBase httpClient;
  private DataStorage cache;

  public void setHttpClient(RestChannelBase httpClient) {
    this.httpClient = httpClient;
  }

  public void setCache(DataStorage cache) {
    this.cache = cache;
  }

  public void download(String url) {
    cache.save(createId(url), httpClient.getStream(url, null, null));
  }

  public InputStream getInputStream(String url, boolean useCache) {
    InputStream stream;
    if (useCache) {
      String id = createId(url);
      stream = cache.getStream(id);
      if (stream == null) {
        download(url);
        stream = cache.getStream(id);
      }
    } else {
      stream = httpClient.getStream(url, null, null);
    }
    return stream;
  }

  private static String createId(String url) {
    // simply take url as id
    return url;
  }
}
