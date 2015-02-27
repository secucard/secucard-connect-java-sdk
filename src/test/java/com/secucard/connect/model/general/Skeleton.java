package com.secucard.connect.model.general;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.secucard.connect.model.SecuObject;

import java.util.List;

public class Skeleton extends SecuObject{
  public static final String OBJECT = "general.skeletons";

  @JsonProperty
  private String a;

  @JsonProperty
  private String b;

  @JsonProperty
  private String c;

  @JsonProperty
  private int amount;

  @JsonProperty
  private String picture;

  @JsonProperty
  private String date;

  @JsonProperty
  private String type;

  @JsonProperty
  private Location location;

  @JsonProperty
  private Skeleton skeleton;

  @JsonProperty("skeleton_list")
  private List<Skeleton> skeletonList;

  @Override
  public String getObject() {
    return OBJECT;
  }

  public String getA() {
    return a;
  }

  public void setA(String a) {
    this.a = a;
  }

  public String getB() {
    return b;
  }

  public void setB(String b) {
    this.b = b;
  }

  public String getC() {
    return c;
  }

  public void setC(String c) {
    this.c = c;
  }

  public int getAmount() {
    return amount;
  }

  public void setAmount(int amount) {
    this.amount = amount;
  }

  public String getPicture() {
    return picture;
  }

  public void setPicture(String picture) {
    this.picture = picture;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  public List<Skeleton> getSkeletonList() {
    return skeletonList;
  }

  public void setSkeletonList(List<Skeleton> skeletonList) {
    this.skeletonList = skeletonList;
  }

  public Skeleton getSkeleton() {
    return skeleton;
  }

  @Override
  public String toString() {
    return "Skeleton{" +
        ", id='" + id + '\'' +
        ", a='" + a + '\'' +
        ", b='" + b + '\'' +
        ", c='" + c + '\'' +
        ", amount=" + amount +
        ", picture='" + picture + '\'' +
        ", date='" + date + '\'' +
        ", type='" + type + '\'' +
        ", location=" + location +
        ", skeleton=" + skeleton +
        ", skeleton_list=" + skeletonList +
        '}';
  }
}
