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

package com.secucard.connect.product.smart.model;

import com.secucard.connect.product.loyalty.model.MerchantCard;

public class ComponentInstruction {

  public static final String COMPONENT_ACTION_OPEN = "open";
  public static final String COMPONENT_ACTION_UPDATE = "update";
  public static final String COMPONENT_ACTION_CLOSE = "close";

  public static final String COMPONENT_TARGET_IDENT_LINK = "ident-link";
  public static final String COMPONENT_TARGET_USER_SELECTION = "user-selection";
  public static final String COMPONENT_TARGET_CHECKIN_BUTTON = "checkin-button";

  public String url;

  public String target;

  public String action;

  public ComponentSize size;

  public ComponentPosition position;

  public MerchantCard merchantcard;

  @Override
  public String toString() {
    return "ComponentInstruction{" + ", url='" + url + '\'' + ", target='" + target + '\'' + ", action='" + action + '\'' + ", size='" + size + '\''
        + ", position='" + position + '\'' + ", merchantcard='" + merchantcard + '\'' + ", " + super.toString() + '}';
  }

}
