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

package com.secucard.connect.net.rest;

import com.secucard.connect.client.Callback;
import com.secucard.connect.client.ClientContext;
import com.secucard.connect.client.ClientError;
import com.secucard.connect.client.NetworkError;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.net.Channel;
import com.secucard.connect.net.JsonMappingException;
import com.secucard.connect.product.common.model.QueryParams;
import com.secucard.connect.util.ExceptionMapper;
import com.secucard.connect.util.Log;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

public abstract class RestChannel extends Channel {
  private final static Log LOG = new Log(RestChannel.class);
  protected final Configuration configuration;

  public RestChannel(Configuration configuration, ClientContext context) {
    super(context);
    this.configuration = configuration;
  }

  /**
   * Low level rest access for internal usage, posting to a url and get the response back as object.
   *
   * @throws com.secucard.connect.net.rest.HttpErrorException if an http error happens.
   * @throws com.secucard.connect.client.ClientError          if an error happens.
   */
  public abstract <T, E> T post(String url, Map<String, Object> parameters, Map<String, String> headers,
                                Class<T> responseType, Class<E> errorResponseType) throws HttpErrorException;

  /**
   * Low level rest access for internal usage, getting response as stream.
   */
  public abstract InputStream getStream(String url, Map<String, Object> parameters, final Map<String, String> headers,
                                        final Callback<InputStream> callback);

  protected Map<String, Object> queryParamsToMap(QueryParams queryParams) {
    Map<String, Object> map = new HashMap<>();

    if (queryParams == null) {
      return map;
    }

    boolean scroll = queryParams.getScrollId() != null && !queryParams.getScrollId().isEmpty();

    if (scroll) {
      map.put("scroll_id", queryParams.getScrollId());
    }

    boolean scrollExpire = StringUtils.isNotBlank(queryParams.getScrollExpire());
    if (scrollExpire) {
      map.put("scroll_expire", queryParams.getScrollExpire());
    }

    if (!scroll && queryParams.getCount() != null && queryParams.getCount() >= 0) {
      map.put("count", queryParams.getCount().toString());
    }

    if (!scroll && !scrollExpire && queryParams.getOffset() != null && queryParams.getOffset() > 0) {
      map.put("offset", queryParams.getOffset().toString());
    }

    List<String> fields = queryParams.getFields();
    if (!scroll && fields != null && fields.size() > 0) {
      // add "," separated list of field names
      String names = null;
      for (String field : fields) {
        names = names == null ? "" : names + ',';
        names += field;
      }
      map.put("fields", names);
    }

    Map<String, String> sortOrder = queryParams.getSortOrder();
    if (!scroll && sortOrder != null) {
      for (Map.Entry<String, String> entry : sortOrder.entrySet()) {
        map.put("sort[" + entry.getKey() + "]", entry.getValue());
      }
    }

    if (StringUtils.isNotBlank(queryParams.getQuery())) {
      map.put("q", queryParams.getQuery());
    }

    if (StringUtils.isNotBlank(queryParams.getPreset())) {
      map.put("preset", queryParams.getPreset());
    }

    QueryParams.GeoQuery gq = queryParams.getGeoQuery();
    if (gq != null) {
      if (StringUtils.isNotBlank(gq.getField())) {
        map.put("geo[field]", gq.getField());
      }

      if (gq.getLat() != null) {
        map.put("geo[lat]", Double.toString(gq.getLat()));
      }

      if (gq.getLon() != null) {
        map.put("geo[lon]", Double.toString(gq.getLon()));
      }

      if (StringUtils.isNotBlank(gq.getDistance())) {
        map.put("geo[distance]", gq.getDistance());
      }
    }

    return map;
  }

  protected String encodeQueryParams(QueryParams queryParams) {
    return encodeQueryParams(queryParamsToMap(queryParams));
  }

  protected String encodeQueryParams(Map<String, Object> queryParams) {
    if (queryParams == null || queryParams.isEmpty()) {
      return null;
    }
    StringBuilder encodedParams = new StringBuilder();
    String paramsEncoding = "UTF-8";
    try {
      for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
        encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
        encodedParams.append('=');
        String value = (String) entry.getValue();
        if (value != null) {
          encodedParams.append(URLEncoder.encode(value, paramsEncoding));
        }
        encodedParams.append('&');
      }
      return encodedParams.toString();
    } catch (UnsupportedEncodingException uee) {
      throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
    }
  }

  /**
   * Adds auth headers if the secure setting is set and not "anonymous" thread local is passed.
   *
   * @param headers The header map to add to.
   */
  @SuppressWarnings({"unchecked"})
  protected void setAuthorizationHeader(Map headers) {
    String token = context.tokenManager.getToken(false);

    if (token != null) {
      String key = "Authorization";
      String value = "Bearer " + token;
      if (headers instanceof MultivaluedMap) {
        ((MultivaluedMap) headers).putSingle(key, value);
      } else {
        headers.put(key, value);
      }
    }
  }

  public void setEventListener(EventListener listener) {
    LOG.info("REST channel doesn't support events.");
  }

  /**
   * Inspect the throwable and skip the ignores http status codes and/or extract error details.
   */
  protected RuntimeException translate(Throwable throwable) {
    ClientError ce = ExceptionMapper.unwrap(throwable, ClientError.class);
    if (ce != null) {
      return ce;
    }

    // IO error caused by parsing, must test before normal IO
    JsonMappingException je = ExceptionMapper.unwrap(throwable, JsonMappingException.class);
    if (je != null) {
      return new ClientError("Failed to read secucard server response: " + je.getJson());
    }

    IOException ie = ExceptionMapper.unwrap(throwable, IOException.class);
    if (ie != null) {
      return new NetworkError(throwable);
    }

    TimeoutException te = ExceptionMapper.unwrap(throwable, TimeoutException.class);
    if (te != null) {
      // timeout is most likely caused by network problem
      return new NetworkError(throwable);
    }

    return null;
  }

  /**
   * REST configuration. Supported properties are:
   * <p/>
   * - rest.url, URL of the secucard REST API.<br/>
   * - rest.responseTimeoutSec, Timeout for getting any response. O for no timeout.<br/>
   * - rest.connectTimeoutSec, Timeout for connecting. O for no timeout.<br/>
   */
  public static class Configuration {
    protected final String baseUrl;
    protected final int responseTimeoutSec;
    protected final int connectTimeoutSec;

    public Configuration(Properties properties) {
      this.baseUrl = properties.getProperty("rest.url");
      this.responseTimeoutSec = Integer.parseInt(properties.getProperty("rest.responseTimeoutSec"));
      this.connectTimeoutSec = Integer.parseInt(properties.getProperty("rest.connectTimeoutSec"));
    }


    @Override
    public String toString() {
      return "REST Configuration{" +
          "baseUrl='" + baseUrl + '\'' +
          ", responseTimeoutSec=" + responseTimeoutSec +
          ", connectTimeoutSec=" + connectTimeoutSec +
          '}';
    }
  }
}
