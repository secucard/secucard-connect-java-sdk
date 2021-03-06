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

package com.secucard.connect.product.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class QueryParams {

  public static final String SORT_ASC = "asc";
  public static final String SORT_DESC = "desc";

  private Integer count;

  private Integer offset;

  @JsonProperty("scroll_id")
  private String scrollId;

  @JsonProperty("scroll_expire")
  private String scrollExpire;

  private List<String> fields;

  @JsonProperty("sort")
  private Map<String, String> sortOrder;

  @JsonProperty("q")
  private String query;

  private String preset;

  @JsonProperty("geo")
  private GeoQuery geoQuery;

  public String getPreset() {
    return preset;
  }

  public void setPreset(String preset) {
    this.preset = preset;
  }

  public Integer getCount() {
    return count;
  }

  public void setCount(Integer count) {
    this.count = count;
  }

  public Integer getOffset() {
    return offset;
  }

  public void setOffset(Integer offset) {
    this.offset = offset;
  }

  public String getScrollId() {
    return scrollId;
  }

  public void setScrollId(String scrollId) {
    this.scrollId = scrollId;
  }

  public String getScrollExpire() {
    return scrollExpire;
  }

  public void setScrollExpire(String scrollExpire) {
    this.scrollExpire = scrollExpire;
  }

  public void setFields(String... fields) {
    if (this.fields == null) {
      this.fields = new ArrayList<>(fields.length);
    }
    this.fields.addAll(Arrays.asList(fields));
  }

  public List<String> getFields() {
    return fields;
  }

  public Map<String, String> getSortOrder() {
    return sortOrder;
  }

  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query;
  }

  public void addSortOrder(String field, String order) {
    if (sortOrder == null) {
      sortOrder = new HashMap<>();
    }
    sortOrder.put(field, order);
  }

  public GeoQuery getGeoQuery() {
    return geoQuery;
  }

  public void setGeoQuery(GeoQuery geoQuery) {
    this.geoQuery = geoQuery;
  }

  public static class GeoQuery {

    private String field;

    private String distance;

    private Double lat;

    private Double lon;

    public GeoQuery() {
    }

    public GeoQuery(String field, Double lat, Double lon, String distance) {
      this.field = field;
      this.distance = distance;
      this.lat = lat;
      this.lon = lon;
    }

    public GeoQuery(Double lat, Double lon, String distance) {
      this.distance = distance;
      this.lat = lat;
      this.lon = lon;
    }

    public String getField() {
      return field;
    }

    public void setField(String field) {
      this.field = field;
    }

    public String getDistance() {
      return distance;
    }

    public void setDistance(String distance) {
      this.distance = distance;
    }

    public Double getLat() {
      return lat;
    }

    public void setLat(Double lat) {
      this.lat = lat;
    }

    public Double getLon() {
      return lon;
    }

    public void setLon(Double lon) {
      this.lon = lon;
    }
  }
}
