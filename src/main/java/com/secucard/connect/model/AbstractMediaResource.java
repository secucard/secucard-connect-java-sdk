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

  protected AbstractMediaResource() {
  }

  protected AbstractMediaResource(String url) throws MalformedURLException {
    setUrl(url);
  }

  @JsonProperty
  protected void setUrl(String url) throws MalformedURLException {
    this.url = new URL(url);
  }

  @JsonProperty
  public String getUrl() {
    return url.toString();
  }

  public void setStorage(DataStorage storage) {
    this.storage = storage;
  }

  /**
   * Downloading resource and store for later access.
   *
   * @throws IOException if a error ocurrs during download or storing.
   */
  public void download() throws IOException {
    if (storage != null && url != null) {
      put2cache(openInputStream());
    }
  }

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


  public InputStream getInputStream() throws IOException {
    if (url == null) {
      return null;
    }

    if (storage == null) {
      return openInputStream();
    }

    Object resource = storage.get(getUrl());
    if (resource == null || !(resource instanceof InputStream)) {
      put2cache(openInputStream());
      resource = storage.get(getUrl());
    }
    return (InputStream) resource;
  }

  protected InputStream openInputStream() throws IOException {
    return url.openConnection().getInputStream();
  }

  protected void put2cache(InputStream is) {
    storage.save(getUrl(), is);
  }
}
