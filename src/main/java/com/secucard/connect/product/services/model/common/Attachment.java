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

package com.secucard.connect.product.services.model.common;

import com.secucard.connect.product.common.model.MediaResource;

import java.net.MalformedURLException;

public class Attachment extends MediaResource {
  private String type;

  public Attachment() {
  }

  public Attachment(String url, String type) throws MalformedURLException {
    super(url);
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return "Attachment{" +
        "type='" + type + '\'' +
        ", url='" + getUrl() + '\'' +
        '}';
  }
}
