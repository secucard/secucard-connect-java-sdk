package com.secucard.connect.model;

import java.io.Serializable;

public abstract class SecuObject implements Serializable {
  // Name of properties of this object, avoids direct string usage.
  public static final String OBJECT_PROPERTY = "object";
  public static final String OBJECT_FIELD = "OBJECT";
  public static final String ID_PROPERTY = "id";

  protected String id;

  private String object;

  //public String demo; // todo: remove in release, just for testing purposes!

  /**
   * Returns the unique resource/object/product identifier, all lowercase.<br/>
   * Override in subclasses to return a constant value using:<br/>
   * {@code public static final String OBJECT = "service.product";}<br/>
   * Alternatively use {@link com.secucard.connect.model.annotation.ProductInfo} annotation.<br/>
   * Note: This fixed object id will be used for building request paths, object mapping purposes, etc, so correct
   * implementation in subclasses is crucial. <br/>
   */
  public String getObject() {
    return object;
  }

  public void setObject(String object) {
    this.object = object;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "SecuObject{id='" + id + "', object='" + getObject() + '}';
  }
}
