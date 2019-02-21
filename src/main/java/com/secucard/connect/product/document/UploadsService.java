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
import com.secucard.connect.util.CallbackAdapter;
import com.secucard.connect.util.Converter;

/**
 * Implements the document/uploads operations.
 */

public class UploadsService extends ProductService<Upload> {

  public static final ServiceMetaData<Upload> META_DATA = new ServiceMetaData<>("document", "uploads", Upload.class);

  /**
   * Upload the given document and returns the new id for the upload. Note: the uploaded content should be base64 encoded.
   */
  public String upload(Upload content, Callback<String> callback) {
    Converter<Upload, String> conv = new Converter<Upload, String>() {
      @Override
      public String convert(Upload value) {
        return value.getId();
      }
    };
    CallbackAdapter<Upload, String> cb = callback == null ? null : new CallbackAdapter<>(callback, conv);
    Upload result = super.execute(null, null, null, content, Upload.class, null, cb);
    return conv.convert(result);
  }

  @Override
  public ServiceMetaData<Upload> getMetaData() {
    return META_DATA;
  }

}
