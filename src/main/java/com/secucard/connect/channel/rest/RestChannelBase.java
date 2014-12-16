package com.secucard.connect.channel.rest;

import com.secucard.connect.auth.OAuthClientCredentials;
import com.secucard.connect.auth.OAuthUserCredentials;
import com.secucard.connect.channel.AbstractChannel;
import com.secucard.connect.channel.JsonMapper;
import com.secucard.connect.event.EventListener;
import com.secucard.connect.model.general.components.Geometry;
import com.secucard.connect.model.transport.QueryParams;
import com.secucard.connect.storage.DataStorage;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class RestChannelBase extends AbstractChannel {
  protected final Configuration configuration;
  protected UserAgentProvider userAgentProvider = new UserAgentProvider();
  protected JsonMapper jsonMapper = new JsonMapper();
  protected DataStorage storage;
  protected String id;

  public RestChannelBase(Configuration configuration, String id) {
    this.configuration = configuration;
    this.id = id;
  }

  protected Map<String, String> createAuthParams(OAuthClientCredentials clientCredentials,
                                                 OAuthUserCredentials userCredentials, String refreshToken) {
    Map<String, String> parameters = new HashMap<>();
    parameters.put("client_id", clientCredentials.getClientId());
    parameters.put("client_secret", clientCredentials.getClientSecret());
    if (refreshToken != null) {
      parameters.put("grant_type", "refresh_token");
      parameters.put("refresh_token", refreshToken);
    } else if (userCredentials != null) {
      parameters.put("grant_type", "appuser");
      parameters.put("username", userCredentials.getUsername());
      parameters.put("password", userCredentials.getPassword());
    } else {
      parameters.put("grant_type", "client_credentials");
    }
    return parameters;
  }

  public void setStorage(DataStorage storage) {
    this.storage = storage;
  }

  @Override
  public void setEventListener(EventListener listener) {
    throw new UnsupportedOperationException("Rest channel doesn't support listener.");
  }

  protected Map<String, String> queryParamsToMap(QueryParams queryParams) {
    Map<String, String> map = new HashMap<>();

    boolean scroll = queryParams.getScrollId() != null && Integer.parseInt(queryParams.getScrollId()) > 0;

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
      if (StringUtils.isNotBlank(gq.getFieldName())) {
        map.put("geo[field]", gq.getFieldName());
      }

      Geometry geometry = gq.getGeometry();
      if (geometry != null) {
        if (geometry.getLat() != 0) {
          map.put("geo[lat]", Double.toString(geometry.getLat()));
        }
        if (geometry.getLon() != 0) {
          map.put("geo[lon]", Double.toString(geometry.getLon()));
        }
      }

      if (StringUtils.isNotBlank(gq.getDistance())) {
        map.put("geo[distance]", gq.getDistance());
      }
    }

    return map;
  }

    protected String queryParamsToString(QueryParams queryParams) {
       String paramString = "";

        boolean scroll = queryParams.getScrollId() != null && !queryParams.getScrollId().equals("");
        if (scroll) {
            paramString += "&scroll_id="+queryParams.getScrollId();
        }

        boolean scrollExpire = StringUtils.isNotBlank(queryParams.getScrollExpire());
        if (scrollExpire) {
            paramString += "&scroll_expire="+queryParams.getScrollExpire();
        }

        if (!scroll && queryParams.getCount() != null && queryParams.getCount() >= 0) {
            paramString += "&count="+queryParams.getCount().toString();
        }

        if (!scroll && !scrollExpire && queryParams.getOffset() != null && queryParams.getOffset() > 0) {
            paramString += "&offset="+queryParams.getOffset().toString();
        }

        List<String> fields = queryParams.getFields();
        if (!scroll && fields != null && fields.size() > 0) {
            // add "," separated list of field names
            String names = null;
            for (String field : fields) {
                names = names == null ? "" : names + ',';
                names += field;
            }
            paramString += "&fields="+names;
        }

        Map<String, String> sortOrder = queryParams.getSortOrder();
        if (!scroll && sortOrder != null) {
            for (Map.Entry<String, String> entry : sortOrder.entrySet()) {
                paramString += "&sort[" + entry.getKey() + "]="+entry.getValue();
            }
        }

        if (StringUtils.isNotBlank(queryParams.getQuery())) {
            paramString += "&q="+queryParams.getQuery();
        }

        if (StringUtils.isNotBlank(queryParams.getPreset())) {
            paramString += "&preset="+queryParams.getPreset();
        }

        // todo: check with scroll id etc.
        QueryParams.GeoQuery gq = queryParams.getGeoQuery();
        if (gq != null) {

            // sort[_geometry]=asc
            // geo[lat]=51.175214&geo[lon]=14.027788&geo[field]=geometry
            // geo[distance]=1000m

            if (StringUtils.isNotBlank(gq.getFieldName())) {
                paramString += "&geo[field]="+gq.getFieldName();
            }

            Geometry geometry = gq.getGeometry();
            if (geometry != null) {
                if (geometry.getLat() != 0) {
                    paramString += "&geo[lat]="+Double.toString(geometry.getLat());
                }
                if (geometry.getLon() != 0) {
                    paramString += "&geo[lon]="+Double.toString(geometry.getLon());
                }
            }

            if (StringUtils.isNotBlank(gq.getDistance())) {
                paramString += "&geo[distance]="+gq.getDistance();
            }
        }

        return paramString;
    }
}
