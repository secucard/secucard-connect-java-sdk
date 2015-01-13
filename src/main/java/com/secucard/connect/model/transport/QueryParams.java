package com.secucard.connect.model.transport;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.model.general.components.Geometry;

import java.util.*;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class QueryParams {
  public static final String SORT_ASC = "asc";
  public static final String SORT_DESC = "desc";

  private Integer count;

  private Integer offset = 1;

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

  @JsonIgnore
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
    private String fieldName;
    private String distance;
    private Geometry geometry;

    public GeoQuery(String fieldName, Geometry geometry, String distance) {
      this.fieldName = fieldName;
      this.distance = distance;
      this.geometry = geometry;
    }

    public String getDistance() {
      return distance;
    }

    public void setDistance(String distance) {
      this.distance = distance;
    }

    public String getFieldName() {
      return fieldName;
    }

    public void setFieldName(String fieldName) {
      this.fieldName = fieldName;
    }

    public Geometry getGeometry() {
      return geometry;
    }

    public void setGeometry(Geometry geometry) {
      this.geometry = geometry;
    }
  }
}
