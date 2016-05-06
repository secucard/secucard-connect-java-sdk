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

package com.secucard.connect.product.loyalty;

import com.secucard.connect.client.ProductService;
import com.secucard.connect.net.Options;
import com.secucard.connect.product.loyalty.model.MerchantCard;

/**
 * Implements the loyalty/merchantcards operations.
 */
public class MerchantCardsService extends ProductService<MerchantCard> {

  public static final ServiceMetaData<MerchantCard> META_DATA = new ServiceMetaData<>("loyalty", "merchantcards",
      MerchantCard.class);

  @Override
  public ServiceMetaData<MerchantCard> getMetaData() {
    return META_DATA;
  }

  @Override
  public Options getDefaultOptions() {
    return new Options(Options.CHANNEL_STOMP);
  }

}