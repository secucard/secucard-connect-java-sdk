package com.secucard.connect.util;

import com.secucard.connect.SecuException;
import com.secucard.connect.channel.rest.RestChannelBase;
import com.secucard.connect.storage.DataStorage;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.util.regex.Pattern;

/**
 * Retrieves resources via HTTP and stores in local cache.
 * Later requests are served from the cache.
 */
public class ResourceDownloader {
  boolean retry = false;
  private RestChannelBase httpClient;
  private DataStorage cache;
  private static final Pattern INVALID_CHARS_PATTERN = Pattern.compile("[\\/:*?\"<>|\\.&]+", Pattern.DOTALL);

  private static final ResourceDownloader instance = new ResourceDownloader();

  private ResourceDownloader() {
  }

  public static ResourceDownloader get() {
    return instance;
  }

  public void setHttpClient(RestChannelBase httpClient) {
    this.httpClient = httpClient;
  }

  public void setCache(DataStorage cache) {
    this.cache = cache;
  }

  /**
   * Retrieve a resource and store in cache.
   * Overrides existing resources with same URL.
   *
   * @param url HTTP URL of the resource to read.
   */
  public void download(String url) {
    InputStream stream;
    int count = 0;
    Exception ex = null;
    do {
      try {
        stream = getInputStream(url, false);
      } catch (Exception e) {
        // todo check out which exception are subject to retry, retry is disabled until
        stream = null;
        ex = e;
      }
    } while (retry && stream == null && count++ < 2);

    if (ex != null) {
      throw new SecuException("Unable to download resource from " + url, ex);
    }

    if (stream != null) {
      cache.save(createId(url), stream);
    }
  }

  /**
   * Returns an input stream to a resource to read from.
   * The resources contents will be cached during this. Later access is served from cache.
   *
   * @param url      HTTP URL of the resource to read.
   * @param useCache If true the resource is loaded from the cache, if exist, else not.
   * @return An input stream to read from.
   */
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
      stream = httpClient.getStream(url, null, null, null);
    }
    return stream;
  }

  private static String createId(String url) {
    String s = INVALID_CHARS_PATTERN.matcher(url).replaceAll("");
    if (s.length() > 120) {
      s = StringUtils.substring(s, 0, 120);
    }
    return s;
  }
}
