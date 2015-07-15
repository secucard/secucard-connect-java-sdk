package com.secucard.connect.product.document;

import com.secucard.connect.client.Callback;
import com.secucard.connect.client.ProductService;
import com.secucard.connect.product.document.model.Upload;

public class UploadsService extends ProductService<Upload> {
  /**
   * Upload a Base64 encoded document
   *
   * @param base64EncodeDocument Base64 encoded document
   * @return JSONObject with id
   */
  public Upload uploadDocument(Upload base64EncodeDocument, Callback<Upload> callback) {
    return super.execute(null, null, null, base64EncodeDocument, Upload.class, null, callback);
  }

  @Override
  protected ServiceMetaData<Upload> createMetaData() {
    return new ServiceMetaData<>("document", "uploads", Upload.class);
  }

}
