package com.secucard.connect.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.storage.DataStorage;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class AbstractMediaResource {
  @JsonIgnore
  private URL url;

  @JsonIgnore
  private DataStorage storage;

  @JsonIgnore
  private boolean downloaded = false;

  @JsonIgnore
  private boolean cachingEnabled = true;

  protected AbstractMediaResource() {
  }

  protected AbstractMediaResource(String url) throws MalformedURLException {
    setUrl(url);
  }

  @JsonProperty
  protected void setUrl(String url) throws MalformedURLException {
    this.url = new URL(url);
    downloaded = false;
  }

  @JsonProperty
  public String getUrl() {
    return url.toString();
  }

  public void setStorage(DataStorage storage) {
    this.storage = storage;
  }

  /**
   * Returns if this resource was already downloaded.
   * Note: If this instances URL is changed the flag is cleared.
   */
  public boolean isDownloaded() {
    return downloaded;
  }

  /**
   * Returns if this instances content can be cached
   */
  public boolean isCachable() {
    return storage != null && cachingEnabled && url != null;
  }

  /**
   * Set if caching is enabled or not. Default is enabled.
   */
  public void enableCaching(boolean value) {
    cachingEnabled = value;
  }

  /**
   * Downloading resource and put in cache for later access.
   * Use {@link #downloaded} and {@link #isCachable()} to determine if this was already downloaded before or
   * if caching is possible at all.
   *
   * @throws IOException if a error ocurrs during download or storing.
   */
  public void download() throws IOException {
    if (isCachable()) {
      put2cache(openInputStream());
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
   * Loads this ressource as a stream from its URL.<br/>
   * Note: If this instance is cachable (check {@link #isCachable()} the resource content is also downloaded and cached
   * (if not already happened before, check {@link #isDownloaded()}) and further invocations deliver from cache.
   * Set {@link #enableCaching(boolean)} if this behaviour is not wanted.
   *
   * @return The input stream, or null if this resource has no URL.
   * @throws IOException If a error ocurrs during resource access.
   */
  public InputStream getInputStream() throws IOException {
    if (isCachable()) {
      Object resource = storage.get(getUrl());
      if (resource == null || !(resource instanceof InputStream)) {
        put2cache(openInputStream());
        resource = storage.get(getUrl());
      }
      return (InputStream) resource;
    }

    return url == null ? null : openInputStream();
  }

  protected InputStream openInputStream() throws IOException {
    return url.openConnection().getInputStream();
  }

  protected void put2cache(InputStream is) {
    storage.save(getUrl(), is);
    downloaded = true;
  }
}
