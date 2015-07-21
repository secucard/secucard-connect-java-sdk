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

package com.secucard.connect.product.services;

import com.secucard.connect.client.Callback;
import com.secucard.connect.client.ProductService;
import com.secucard.connect.event.EventHandlerCallback;
import com.secucard.connect.event.Events;
import com.secucard.connect.net.Options;
import com.secucard.connect.product.common.model.ObjectList;
import com.secucard.connect.product.common.model.QueryParams;
import com.secucard.connect.product.general.model.Event;
import com.secucard.connect.product.services.model.IdentRequest;
import com.secucard.connect.product.services.model.IdentResult;
import com.secucard.connect.product.services.model.common.Attachment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Implements the servcies/identresult operations.
 * Provides access to the secucard services resources and operations.
 * Support also caching of attachments when requested.
 * Note: Caching will speed up clients access, but has also impact on memory usage. Depending on the type of cache storage
 * it may be not a good idea to cache too much data. The {@link com.secucard.connect.client.DataStorage) implementation
 * provided to the client is used also for caching.
 * See {@link com.secucard.connect.SecucardConnect.Configuration to get details which storage implementation is actually used.
 */
public class IdentResultsService extends ProductService<IdentResult> {
  private boolean cacheAttachmentsEnabled = true;

  @Override
  protected ServiceMetaData<IdentResult> createMetaData() {
    return new ServiceMetaData<>("services", "identresults", IdentResult.class);
  }

  /**
   * Set to true/false to globally enable/disable attachment caching when requested by methods of this service.
   * Caching is enabled by default but is only performed when requested in methods.
   */
  public void cacheAttachments(boolean cacheAttachments) {
    this.cacheAttachmentsEnabled = cacheAttachments;
  }

  /**
   * Returns a ident result for a given  ident request ids.
   * No exception is thrown when a callback was provided.
   *
   * @param identRequestIds     The request ids to get the results for.
   * @param callback            Callback for asynchronous handling.
   * @param downloadAttachments Set to true if attachments should be completely downloaded before returning.
   *                            Note: Depending on the number of returned persons + attachments this may be a lot!
   *                            Works only if {@link #cacheAttachments(boolean)} is set to true, which is the default.
   * @return The ident results or null if a callback was provided.
   * @throws com.secucard.connect.client.SecucardConnectException I fan error happens.
   */
  public List<IdentResult> getListByRequestIds(final List<String> identRequestIds,
                                               Callback<List<IdentResult>> callback,
                                               final boolean downloadAttachments) {
    // todo: better avoid query and access by id?

    StringBuilder query = new StringBuilder();
    for (Iterator<String> iterator = identRequestIds.iterator(); iterator.hasNext(); ) {
      String id = iterator.next();
      query.append("request.id:").append(id);
      if (iterator.hasNext()) {
        query.append(" or ");
      }
    }
    QueryParams params = new QueryParams();
    params.setQuery(query.toString());
    return getSimpleList(params, callback, downloadAttachments);
  }

  /**
   * Returns a list of ident result objects according to the given query parameters.
   * Supports also the optional download and caching of all attachments before returning.
   * This will speed up clients access to them but involves increasing memory usage.
   * No exception is thrown when a callback was provided.
   *
   * @param queryParams         A set of parameters a ident result must match.
   * @param callback            Callback for asynchronous handling.
   * @param downloadAttachments Set to true if attachments should be completely downloaded before returning.
   *                            Note: Depending on the number of returned results + persons + attachments this may be a lot!
   *                            Works only if {@link #cacheAttachments(boolean)} is set to true, which is the default.
   * @return The ident results or null if a callback was provided.
   * @throws com.secucard.connect.client.SecucardConnectException If an error happens executing the service.
   */
  public List<IdentResult> getSimpleList(final QueryParams queryParams, Callback<List<IdentResult>> callback,
                                         final boolean downloadAttachments) {
    Options opts = getDefaultOptions();
    opts.resultProcessing = new Callback.Notify<ObjectList<IdentResult>>() {
      @Override
      public void notify(ObjectList<IdentResult> result) {
        if (result != null && result.getList() != null) {
          processAttachments(result.getList(), downloadAttachments);
        }
      }
    };
    return super.getSimpleList(queryParams, opts, callback);
  }

  /**
   * Returns a single ident result object.
   * No exception is thrown when a callback was provided.
   *
   * @param id                  The
   * @param callback            Callback for asynchronous handling.
   * @param downloadAttachments Set to true if attachments should be completely downloaded before returning.
   *                            Note: Depending on the number of returned persons + attachments this may be a lot!
   *                            Works only if {@link #cacheAttachments(boolean)} is set to true, which is the default.
   * @return The ident result or null if a callback was provided.
   * @throws com.secucard.connect.client.SecucardConnectException If an error happens executing the service.
   */
  public IdentResult get(final String id, Callback<IdentResult> callback, final boolean downloadAttachments) {
    Options opts = getDefaultOptions();
    opts.resultProcessing = new Callback.Notify<ObjectList<IdentResult>>() {
      @Override
      public void notify(ObjectList<IdentResult> result) {
        if (result != null && result.getList() != null) {
          processAttachments(result.getList(), downloadAttachments);
        }
      }
    };
    return get(id, opts, callback);
  }

  /**
   * Attach an event handler to get notified when IdentRequest changed (approved, canceled etc)
   * This handler will be called then when the event is passed to
   * {@link com.secucard.connect.SecucardConnect#handleEvent(String, boolean)}
   * The event is discarded if no handler is registered.<br/>
   * Note: Registering a handler multiple times just replaces the previous instance.
   *
   * @param handler The handler instance or null to remove the handler.
   * @see {@link IdentEventHandler}.
   */
  public void onIdentRequestChanged(IdentEventHandler handler) {
    if (handler != null) {
      handler.service = this;
    }
    context.eventDispatcher.registerListener(getMetaData().getObject() + Events.TYPE_CHANGED, handler);
  }

  private void processAttachments(List<IdentResult> results, boolean cache) {
    if (cacheAttachmentsEnabled && cache) {
      for (IdentResult result : results) {
        for (IdentResult.Person person : result.getPersons()) {
          for (Attachment attachment : person.getAttachments()) {
            // todo: introduce download policy settings to be able to avoid some downloads
            attachment.download();
          }
        }
      }
    }
  }

  /**
   * This handler retrieves and returns a list of belonging IdentResults for an IdentRequest and downloads the
   * containing attachments when required if such an event is passed to
   * {@link com.secucard.connect.SecucardConnect#handleEvent(String, boolean)}.
   */
  public static abstract class IdentEventHandler extends EventHandlerCallback<List<IdentRequest>, List<IdentResult>> {
    protected IdentResultsService service;


    @Override
    public boolean accept(Event<List<IdentRequest>> event) {
      return service.getMetaData().getObject().equals(event.getTarget()) && Events.TYPE_CHANGED.equals(event.getType());
    }

    @Override
    protected List<IdentResult> process(Event<List<IdentRequest>> event) {
      List<IdentRequest> requests = event.getData();
      List<String> ids;
      if (requests == null) {
        ids = Collections.emptyList();
      } else {
        ids = new ArrayList<>(requests.size());
        for (IdentRequest request : requests) {
          ids.add(request.getId());
        }
      }

      return service.getListByRequestIds(ids, null, downloadAttachments(requests));
    }

    /**
     * Indicates if the idents result attachments associated with the given idents requests should be downloaded or not.
     */
    public abstract boolean downloadAttachments(List<IdentRequest> requests);
  }
}
