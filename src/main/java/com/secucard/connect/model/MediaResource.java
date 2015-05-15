package com.secucard.connect.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.secucard.connect.util.ResourceDownloader;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Base class for all URL based media resources like images or pdf documents.
 * Supports caching of the resource denoted by the URL of this instance. That means the content is downloaded and
 * put to the cache on demand. Further access is served by the cache.<br/>
 * Note: This is not a caching by LRU strategy or alike. If enabled the content will be
 * cached for new instances or when the URL of the instance was changed (its eventually the same).
 */
public class MediaResource implements Serializable{
  private String url;

  @JsonIgnore
  private boolean isCached = false;

  @JsonIgnore
  private boolean cachingEnabled = true;

  public MediaResource() {
  }

  public static MediaResource create(String value) {
    if (value != null) {
      try {
        return new MediaResource(value);
      } catch (MalformedURLException e) {
        // ignore here, value could be just an id as well
      }
    }
    return null;
  }

  public MediaResource(String url) throws MalformedURLException {
    new URL(url); // validate
    setUrl(url);
  }

  public String getUrl() {
    return url;
  }

  protected ResourceDownloader getDownloader() {
//    return ClientContext.get().getResourceDownloader();
    return ResourceDownloader.get();
  }

  public void setUrl(String url) {
    this.url = url;
    isCached = false;
  }

  /**
   * Returns if this resource was already downloaded and cached.
   * Note: If this instances URL is changed the flag is reset.
   */
  public boolean isCached() {
    return isCached;
  }

  /**
   * Set if caching is enabled or not. Default is enabled.
   */
  public void enableCaching(boolean value) {
    cachingEnabled = value;
  }

  /**
   * Returns if this instances content should be cached.
   */
  public boolean isCachingEnabled() {
    return cachingEnabled;
  }

  /**
   * Downloading resource and put in cache for later access no matter if this was already done before.
   * Call {@link #isCached} before to determine if this is the case.
   */
  public void download() {
    if (cachingEnabled && getDownloader() != null) {
      getDownloader().download(url);
      isCached = true;
    }
  }

  /**
   * Return the contents of this resource as byte array.
   * See also {@link #getInputStream()}, the same principle applies here.
   *
   * @return The bytes.
   * @throws IOException If a error ocurrs during resource access.
   */
  public byte[] getContents() throws IOException {
    InputStream in = getInputStream();
    if (in == null) {
      return null;
    }
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try {
      BufferedInputStream bis = new BufferedInputStream(in);
      int b;
      while ((b = bis.read()) != -1) {
        out.write(b);
      }
    } finally {
      in.close();
    }
    return out.toByteArray();
  }

  /**
   * Loads this resource as a stream from its URL.<br/>
   * Note: If caching is enabled, (check {@link #isCachingEnabled()}, the resource content is also downloaded and cached
   * (if not already happened before, check {@link #isCached()}) and further invocations deliver from cache.
   * Set {@link #enableCaching(boolean)} if this behaviour is not wanted.
   *
   * @return The input stream, or null if this resource has no URL.
   */
  public InputStream getInputStream() {
    if (cachingEnabled && !isCached) {
      // force download if not cached
      download();
    }

    if (getDownloader() == null) {
      return null;
    }

    return getDownloader().getInputStream(url, cachingEnabled);
  }
}
