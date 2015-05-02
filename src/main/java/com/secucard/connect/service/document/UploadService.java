package com.secucard.connect.service.document;

import com.secucard.connect.Callback;
import com.secucard.connect.model.document.uploads.Document;
import com.secucard.connect.service.AbstractService;

public class UploadService extends AbstractService {

  /**
   * Upload a Base64 encoded document
   *
   * @param base64EncodeDocument Base64 encoded document
   * @return JSONObject with id
   */
  public Document uploadDocument(Document base64EncodeDocument, Callback<Document> callback) {

    return new ServiceTemplate().execute(Document.class, null, null, null, base64EncodeDocument, Document.class, callback);
  }

}
