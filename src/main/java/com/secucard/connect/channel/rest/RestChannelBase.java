package com.secucard.connect.channel.rest;

import com.secucard.connect.Callback;
import com.secucard.connect.auth.AuthException;
import com.secucard.connect.channel.Channel;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.model.QueryParams;
import com.secucard.connect.util.ThreadLocalUtil;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.core.MultivaluedMap;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class RestChannelBase extends Channel {
  protected final Configuration configuration;
  protected boolean secure = true;

  public RestChannelBase(Configuration configuration, String id) {
    this.id = id;
    this.configuration = configuration;
  }

  @Override
  public void setEventListener(EventListener listener) {
    throw new UnsupportedOperationException("Rest channel doesn't support listener.");
  }

  public void setSecure(boolean secure) {
    this.secure = secure;
  }

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
    Boolean anonymous = (Boolean) ThreadLocalUtil.get("anonymous");
    if ((anonymous != null && anonymous.equals(Boolean.TRUE)) || !secure) {
      return;
    }

    String token = authProvider.getToken(false);

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

  /**
   * Low level rest access for internal usage, posting to a url and get the response back as object.
   */
  public abstract <T> T post(String url, Map<String, Object> parameters, Map<String, String> headers, Class<T> responseType,
                             Integer... ignoredState);

  public abstract InputStream getStream(String url, Map<String, Object> parameters, Map<String, String> headers,
                                        Callback<InputStream> callback);


  public static class Configuration {
    protected final String baseUrl;

    public Configuration(String baseUrl) {
      this.baseUrl = baseUrl;
    }
  }
}
