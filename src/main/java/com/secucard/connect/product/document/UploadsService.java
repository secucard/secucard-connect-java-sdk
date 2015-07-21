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
