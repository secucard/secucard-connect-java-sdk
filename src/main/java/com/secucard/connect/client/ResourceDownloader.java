/*
 * Copyright (c) 2015. hp.weber GmbH & Co secucard KG (www.secucard.com)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.secucard.connect.client;

import com.secucard.connect.net.rest.RestChannel;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.util.regex.Pattern;

/**
 * Retrieves resources via HTTP and stores in local cache.
 * Later requests are served from the cache.
 */
public class ResourceDownloader {
  private static final Pattern INVALID_CHARS_PATTERN = Pattern.compile("[\\/:*?\"<>|\\.&]+", Pattern.DOTALL);
  private static final ResourceDownloader instance = new ResourceDownloader();
  boolean retry = false;
  private RestChannel httpClient;
  private DataStorage cache;
  private boolean enabled = true;

  private ResourceDownloader() {
  }

  public static ResourceDownloader get() {
    if (!instance.enabled) {
      return null;
    }
    return instance;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  private static String createId(String url) {
    String s = INVALID_CHARS_PATTERN.matcher(url).replaceAll("");
    if (s.length() > 120) {
      s = StringUtils.substring(s, 0, 120);
    }
    return s;
  }

  public void setHttpClient(RestChannel httpClient) {
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

        stream = httpClient.getStream(url, null, null, null);

      } catch (Exception e) {
        // todo check out which exception are subject to retry, retry is disabled until
        stream = null;
        ex = e;
      }
    } while (retry && stream == null && count++ < 2);

    if (ex != null) {
      throw new ClientError("Unable to download resource from " + url, ex);
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
}
