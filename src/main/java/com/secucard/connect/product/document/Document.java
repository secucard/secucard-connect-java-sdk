package com.secucard.connect.product.document;

public class Document {
  public Document(UploadsService uploads) {
    this.uploads = uploads;
  }

  public static Class<UploadsService> Uploads = UploadsService.class;
  public UploadsService uploads;
}
