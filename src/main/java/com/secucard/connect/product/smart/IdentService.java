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

package com.secucard.connect.product.smart;

import com.secucard.connect.client.Callback;
import com.secucard.connect.client.ProductService;
import com.secucard.connect.event.AbstractEventListener;
import com.secucard.connect.event.DelegatingEventHandlerCallback;
import com.secucard.connect.event.Events;
import com.secucard.connect.product.common.model.ObjectList;
import com.secucard.connect.product.general.model.Event;
import com.secucard.connect.product.smart.model.ComponentInstruction;
import com.secucard.connect.product.smart.model.Ident;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;

/**
 * Implements the smart/idents operations.
 */
public class IdentService extends ProductService<Ident> {

  public static final ServiceMetaData<Ident> META_DATA = new ServiceMetaData<>("smart", "idents", Ident.class);

  @Override
  public ServiceMetaData<Ident> getMetaData() {
    return META_DATA;
  }

  /**
   * Returns all idents in the system or null if nothing found.
   */
  public ObjectList<Ident> getList(Callback<ObjectList<Ident>> callback) {
    return super.getList(null, null, callback);
  }

  public List<Ident> getSimpleList(Callback<List<Ident>> callback) {
    return super.getSimpleList(null, null, callback);
  }

  /**
   * Read an ident with a given id from a connected device.
   */
  public Ident readIdent(String id, Callback<Ident> callback) {
    return super.execute(id, "read", null, null, Ident.class, null, callback);
  }

  /**
   * Set a callback to get notified when a component gets changed
   */
  public void onChanged(final Callback<List<ComponentInstruction>> callback) {
    AbstractEventListener listener = null;

    if (callback != null) {
      listener = new DelegatingEventHandlerCallback<Event, List<ComponentInstruction>>(callback) {
        @Override
        public boolean accept(Event event) {
          return Events.TYPE_CHANGED.equals(event.getType()) && getMetaData().getObject().equals(event.getTarget());
        }

        @Override
        protected List<ComponentInstruction> process(Event event) {
          return processAllEvents(event);
        }
      };
    }

    context.eventDispatcher.registerListener(getMetaData().getObject() + Events.TYPE_CHANGED, listener);
  }

  private List<ComponentInstruction> processAllEvents(Event event) {
    List<ComponentInstruction> resultList = new ArrayList<>();
    try {
      JSONArray array = new JSONArray(event.getDataRaw());

      for (int i = 0; i < array.length(); i++) {
        resultList.add(event.getJsonMapper().map(array.get(i).toString(), ComponentInstruction.class));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return resultList;
  }
}
