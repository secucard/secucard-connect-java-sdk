package com.secucard.connect.net.rest;

import com.secucard.connect.auth.exception.AuthCanceledException;
import com.secucard.connect.auth.exception.AuthDeniedException;
import com.secucard.connect.auth.exception.AuthFailedException;
import com.secucard.connect.auth.exception.AuthTimeoutException;
import com.secucard.connect.client.Callback;
import com.secucard.connect.client.ClientContext;
import com.secucard.connect.client.SecucardConnectException;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.net.Channel;
import com.secucard.connect.product.common.model.QueryParams;
import com.secucard.connect.util.Log;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.core.MultivaluedMap;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public abstract class RestChannel extends Channel {
  private final static Log LOG = new Log(RestChannel.class);
  protected final Configuration configuration;

  public RestChannel(String id, Configuration configuration, ClientContext context) {
    super(id, context);
    this.configuration = configuration;
  }

  /**
   * Low level rest access for internal usage, posting to a url and get the response back as object.
   *
   * @throws com.secucard.connect.net.rest.HttpErrorException     if an http error happens.
   * @throws com.secucard.connect.client.SecucardConnectException if an error happens.
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
        encodedParams.append(URLEncoder.encode((String) entry.getValue(), paramsEncoding));
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
    String token = null;
    try {
      token = context.tokenManager.getToken(false);
    } catch (AuthDeniedException| AuthFailedException | AuthCanceledException | AuthTimeoutException e) {
      throw new SecucardConnectException("Error sending request.", e);
    }

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
   * REST configuration. Supported properties are:
   * <p/>
   * - rest.url (https://connect.secucard.com/api/v2), URL of the secucard REST API.<br/>
   * - rest.responseTimeoutSec (30), Timeout for getting any response. O for no timeout.<br/>
   * - rest.connectTimeoutSec (30), Timeout for connecting. O for no timeout.<br/>
   */
  public static class Configuration {
    protected final String baseUrl;
    protected final int responseTimeoutSec;
    protected final int connectTimeoutSec;

    public Configuration(Properties properties) {
      this.baseUrl = properties.getProperty("rest.url", "https://connect.secucard.com/api/v2");
      this.responseTimeoutSec = Integer.parseInt(properties.getProperty("rest.responseTimeoutSec", "30"));
      this.connectTimeoutSec = Integer.parseInt(properties.getProperty("rest.connectTimeoutSec", "30"));
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